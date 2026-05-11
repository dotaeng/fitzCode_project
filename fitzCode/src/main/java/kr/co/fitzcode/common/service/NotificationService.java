package kr.co.fitzcode.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.co.fitzcode.common.dto.CustomOAuth2User;
import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.dto.NoticeDTO;
import kr.co.fitzcode.common.dto.NotificationDTO;
import kr.co.fitzcode.common.enums.NotificationType;
import kr.co.fitzcode.common.enums.UserRole;
import kr.co.fitzcode.common.mapper.UserNotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final UserNotificationMapper notificationMapper;
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> adminEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Principal principal) {
        String userId = principal != null ? String.valueOf(extractUserId(principal)) : "anonymous-" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitter.onCompletion(() -> {
            log.info("사용자 SSE 연결 종료, userId: {}, 남은 클라이언트 수: {}", userId, userEmitters.size());
            userEmitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            log.info("사용자 SSE 연결 타임아웃, userId: {}, 남은 클라이언트 수: {}", userId, userEmitters.size());
            userEmitters.remove(userId);
        });
        emitter.onError((e) -> {
            log.error("사용자 SSE 연결 오류 발생, userId: {}, 오류: {}, 남은 클라이언트 수: {}", userId, e.getMessage(), userEmitters.size());
            userEmitters.remove(userId);
        });

        userEmitters.put(userId, emitter);
        log.info("새로운 사용자 클라이언트 구독 완료, userId: {}, 현재 연결된 클라이언트 수: {}", userId, userEmitters.size());

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
            log.info("INIT 이벤트 전송 완료, userId: {}", userId);
        } catch (IOException e) {
            log.error("INIT 이벤트 전송 실패, userId: {}, 오류: {}", userId, e.getMessage());
            userEmitters.remove(userId);
        }

        return emitter;
    }

    public SseEmitter subscribeAdmin(Principal principal) {
        String userId = principal != null ? String.valueOf(extractUserId(principal)) : "admin-" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitter.onCompletion(() -> {
            log.info("관리자 SSE 연결 종료, userId: {}, 남은 클라이언트 수: {}", userId, adminEmitters.size());
            adminEmitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            log.info("관리자 SSE 연결 타임아웃, userId: {}, 남은 클라이언트 수: {}", userId, adminEmitters.size());
            adminEmitters.remove(userId);
        });
        emitter.onError((e) -> {
            log.error("관리자 SSE 연결 오류 발생, userId: {}, 오류: {}, 남은 클라이언트 수: {}", userId, e.getMessage(), adminEmitters.size());
            adminEmitters.remove(userId);
        });

        adminEmitters.put(userId, emitter);
        log.info("새로운 관리자 클라이언트 구독 완료, userId: {}, 현재 연결된 클라이언트 수: {}", userId, adminEmitters.size());

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
            log.info("INIT 이벤트 전송 완료, userId: {}", userId);
        } catch (IOException e) {
            log.error("INIT 이벤트 전송 실패, userId: {}, 오류: {}", userId, e.getMessage());
            adminEmitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotificationToAll(String eventName, NoticeDTO noticeDTO) {
        log.info("모든 사용자에게 알림 전송 시작 - 이벤트: {}, 공지사항 제목: {}", eventName, noticeDTO.getTitle());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String jsonData = objectMapper.writeValueAsString(noticeDTO);
            log.info("JSON 데이터 준비: {}", jsonData);

            List<String> deadEmitters = new ArrayList<>();
            for (Map.Entry<String, SseEmitter> entry : userEmitters.entrySet()) {
                String userId = entry.getKey();
                SseEmitter emitter = entry.getValue();
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(jsonData));
                    log.info("이벤트 전송 성공, userId: {}, 이벤트: {}", userId, eventName);
                } catch (IOException e) {
                    log.error("이벤트 전송 실패, userId: {}, 오류: {}", userId, e.getMessage());
                    deadEmitters.add(userId);
                }
            }

            deadEmitters.forEach(userEmitters::remove);
            log.info("연결 정리 후 사용자 클라이언트 수: {}", userEmitters.size());

            saveNotificationsForAllUsers(noticeDTO);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 오류: {}", e.getMessage(), e);
        }
    }

    public void sendNotificationToAllAdmins(String eventName, InquiryDTO inquiryDTO) {
        log.info("모든 관리자에게 알림 전송 시작 - 이벤트: {}, 문의 제목: {}", eventName, inquiryDTO.getSubject());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String jsonData = objectMapper.writeValueAsString(inquiryDTO);
            log.info("JSON 데이터 준비: {}", jsonData);

            List<String> deadEmitters = new ArrayList<>();
            if (adminEmitters.isEmpty()) {
                log.warn("관리자 SSE 클라이언트가 없습니다. 알림 발송 실패.");
            } else {
                for (Map.Entry<String, SseEmitter> entry : adminEmitters.entrySet()) {
                    String userId = entry.getKey();
                    SseEmitter emitter = entry.getValue();
                    try {
                        emitter.send(SseEmitter.event().name(eventName).data(jsonData));
                        log.info("관리자 이벤트 전송 성공, userId: {}, 이벤트: {}, 데이터: {}", userId, eventName, jsonData);
                    } catch (IOException e) {
                        log.error("관리자 이벤트 전송 실패, userId: {}, 오류: {}", userId, e.getMessage());
                        deadEmitters.add(userId);
                    }
                }
                deadEmitters.forEach(adminEmitters::remove);
                log.info("연결 정리 후 관리자 클라이언트 수: {}", adminEmitters.size());
            }

            saveNotificationsForAllAdmins(inquiryDTO);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 오류: {}", e.getMessage(), e);
        }
    }

    public void sendNotificationToUser(String eventName, InquiryDTO inquiryDTO, int userId) {
        log.info("특정 사용자에게 알림 전송 시작 - 이벤트: {}, 사용자 ID: {}, 문의 제목: {}", eventName, userId, inquiryDTO.getSubject());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String jsonData = objectMapper.writeValueAsString(inquiryDTO);
            log.info("JSON 데이터 준비: {}", jsonData);

            String targetUserId = String.valueOf(userId);
            SseEmitter emitter = userEmitters.get(targetUserId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(jsonData));
                    log.info("이벤트 전송 성공, userId: {}, 이벤트: {}", targetUserId, eventName);
                } catch (IOException e) {
                    log.error("이벤트 전송 실패, userId: {}, 오류: {}", targetUserId, e.getMessage());
                    userEmitters.remove(targetUserId);
                }
            } else {
                log.warn("연결된 사용자 클라이언트 없음, userId: {}", targetUserId);
            }

            saveNotificationForUser(inquiryDTO, userId);
            log.info("알림 DB 저장 완료, userId: {}, relatedId: {}", userId, inquiryDTO.getInquiryId());
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 오류: {}", e.getMessage(), e);
        }
    }

    private void saveNotificationForUser(InquiryDTO inquiryDTO, int userId) {
        log.info("사용자 ID: {}에 알림 저장 시작, 문의 ID: {}", userId, inquiryDTO.getInquiryId());
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setType(NotificationType.INQUIRY_RESPONSE.getCode());
        notification.setMessage("1대1 문의 답변이 있습니다");
        notification.setRelatedId(Integer.valueOf(inquiryDTO.getInquiryId()));
        notificationMapper.insertNotification(notification);
        log.info("사용자 ID: {}에 대한 알림 저장 완료, notificationId: {}", userId, notification.getNotificationId());
    }

    private void saveNotificationsForAllAdmins(InquiryDTO inquiryDTO) {
        log.info("모든 관리자에게 알림 저장 시작, 문의 ID: {}", inquiryDTO.getInquiryId());
        List<Integer> adminIds = notificationMapper.getAllAdminIds(UserRole.ADMIN.getCode());
        log.info("조회된 관리자 ID 수: {}", adminIds.size());

        // 중복 관리자 ID 제거
        adminIds = adminIds.stream().distinct().collect(Collectors.toList());
        log.info("중복 제거 후 관리자 ID 수: {}", adminIds.size());

        if (adminIds.isEmpty()) {
            log.warn("관리자 ID가 없습니다. 알림 저장 중단");
            return;
        }

        // 동일한 inquiryId와 adminId 조합에 대해 중복 알림 방지
        for (Integer adminId : adminIds) {
            // 기존 알림 존재 여부 확인
            boolean exists = notificationMapper.existsNotification(adminId, NotificationType.INQUIRY_CREATED.getCode(), inquiryDTO.getInquiryId());
            if (exists) {
                log.info("이미 존재하는 알림: adminId: {}, inquiryId: {}", adminId, inquiryDTO.getInquiryId());
                continue;
            }

            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(adminId);
            notification.setType(NotificationType.INQUIRY_CREATED.getCode());
            notification.setMessage("새로운 1대1 문의가 있습니다.");
            notification.setRelatedId(Integer.valueOf(inquiryDTO.getInquiryId()));
            notificationMapper.insertNotification(notification);
            log.info("관리자 ID: {}에 대한 알림 저장 완료, notificationId: {}", adminId, notification.getNotificationId());
        }
    }

    private void saveNotificationsForAllUsers(NoticeDTO noticeDTO) {
        log.info("모든 사용자에게 알림 저장 시작, 공지사항 ID: {}", noticeDTO.getNoticeId());
        List<Integer> userIds = notificationMapper.getAllUserIds();
        log.info("조회된 사용자 ID 수: {}", userIds.size());
        for (Integer userId : userIds) {
            NotificationDTO notification = new NotificationDTO();
            notification.setUserId(userId);
            notification.setType(NotificationType.NOTICE.getCode());
            notification.setMessage("새로운 공지사항: " + noticeDTO.getTitle());
            notification.setRelatedId(Integer.valueOf(noticeDTO.getNoticeId()));
            notificationMapper.insertNotification(notification);
            log.info("사용자 ID: {}에 대한 알림 저장 완료, notificationId: {}", userId, notification.getNotificationId());
        }
    }

    public void createNotification(NotificationDTO notificationDTO) {
        notificationMapper.insertNotification(notificationDTO);
        log.info("알림 생성 완료, notificationId: {}", notificationDTO.getNotificationId());
    }

    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        return notificationMapper.getNotificationsByUserId(userId);
    }

    public void deleteNotification(Integer notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }

    public void deleteAllNotifications(Integer userId) {
        notificationMapper.deleteAllNotifications(userId);
    }

    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    private int extractUserId(Principal principal) {
        Object principalObj = principal;

        if (principalObj instanceof UsernamePasswordAuthenticationToken) {
            principalObj = ((UsernamePasswordAuthenticationToken) principalObj).getPrincipal();
        } else if (principalObj instanceof OAuth2AuthenticationToken) {
            principalObj = ((OAuth2AuthenticationToken) principalObj).getPrincipal();
        }

        if (principalObj instanceof CustomUserDetails) {
            return ((CustomUserDetails) principalObj).getUserId();
        } else if (principalObj instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principalObj).getUserId();
        } else {
            log.error("알 수 없는 Principal 내부 타입: {}", principalObj.getClass().getName());
            throw new IllegalStateException("알 수 없는 Principal 내부 타입: " + principalObj.getClass().getName());
        }
    }
}
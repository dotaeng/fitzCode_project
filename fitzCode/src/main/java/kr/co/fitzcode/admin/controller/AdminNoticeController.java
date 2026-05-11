package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.exception.InvalidNoticeIdException;
import kr.co.fitzcode.admin.exception.NoticeNotFoundException;
import kr.co.fitzcode.admin.service.NoticeService;
import kr.co.fitzcode.common.dto.CustomOAuth2User;
import kr.co.fitzcode.common.dto.NoticeDTO;
import kr.co.fitzcode.common.service.CustomUserDetails;
import kr.co.fitzcode.common.service.NotificationService;
import kr.co.fitzcode.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
@Slf4j
public class AdminNoticeController {

    private final NoticeService noticeService;
    private final NotificationService notificationService;
    private final S3Service s3Service;
    private final software.amazon.awssdk.services.s3.S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Principal principal) {
        return notificationService.subscribe(principal);
    }

    @PostMapping
    public String createNotice(@ModelAttribute NoticeDTO noticeDTO,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               @RequestParam("attachmentFile") MultipartFile attachmentFile,
                               Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new IllegalStateException("사용자가 인증되지 않았습니다.");
            }

            int userId = extractUserId(auth);
            noticeDTO.setUserId(userId);
            NoticeDTO savedNotice = noticeService.createNotice(noticeDTO, imageFile, attachmentFile);
            log.info("공지사항 생성 완료 - 제목: {}, 공지사항 ID: {}", savedNotice.getTitle(), savedNotice.getNoticeId());
            log.info("NOTICE_CREATED 이벤트 전송 - 공지사항 DTO: {}", savedNotice.getTitle());
            notificationService.sendNotificationToAll("NOTICE_CREATED", savedNotice);
            return "redirect:/admin/notice";
        } catch (Exception e) {
            log.error("공지사항 생성 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @GetMapping
    public String getAllNotices(
            @RequestParam(defaultValue = "1") int page, // 현재 페이지
            @RequestParam(defaultValue = "10") int size, // 페이지당 항목 수
            Model model) {
        try {
            // 페이지 번호는 1부터 시작하므로 0-based로 변환
            int pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }

            // 공지사항 목록 조회 (페이징 적용)
            List<NoticeDTO> notices = noticeService.getNoticesWithPagination(pageIndex, size);
            // 총 공지사항 수 조회
            long totalNotices = noticeService.getTotalNoticeCount();
            // 총 페이지 수 계산
            int totalPages = (int) Math.ceil((double) totalNotices / size);
            if (totalPages == 0) {
                totalPages = 1; // 최소 1페이지 보장
            }

            // 페이지 번호 목록 생성 (최대 5개 페이지 표시)
            int startPage = Math.max(1, page - 2);
            int endPage = Math.min(totalPages, startPage + 4);
            if (endPage - startPage < 4) {
                startPage = Math.max(1, endPage - 4);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());

            // 모델에 데이터 추가
            model.addAttribute("notices", notices);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("size", size);
            return "admin/notice/list";
        } catch (NoticeNotFoundException e) {
            log.error("공지사항 목록 조회 실패: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @GetMapping("/add")
    public String createNoticeForm(Model model) {
        model.addAttribute("notice", new NoticeDTO());
        return "admin/notice/create";
    }

    @GetMapping("/{id}")
    public String getNoticeById(@PathVariable int id, Model model) {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            model.addAttribute("notice", notice);
            return "admin/notice/detail";
        } catch (NoticeNotFoundException e) {
            log.error("공지사항 상세 조회 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @GetMapping("/{id}/edit")
    public String editNoticeForm(@PathVariable int id, Model model) {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            model.addAttribute("notice", notice);
            return "admin/notice/edit";
        } catch (NoticeNotFoundException e) {
            log.error("공지사항 수정 폼 조회 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @PostMapping("/{id}")
    public String updateNotice(@PathVariable int id,
                               @ModelAttribute NoticeDTO noticeDTO,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               @RequestParam("attachmentFile") MultipartFile attachmentFile,
                               Model model) {
        try {
            noticeDTO.setNoticeId(id);
            noticeService.updateNotice(noticeDTO, imageFile, attachmentFile);
            return "redirect:/admin/notice";
        } catch (InvalidNoticeIdException e) {
            log.error("공지사항 업데이트 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteNotice(@PathVariable int id, Model model) {
        try {
            noticeService.deleteNotice(id);
            return "redirect:/admin/notice";
        } catch (InvalidNoticeIdException e) {
            log.error("공지사항 삭제 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadAttachment(@PathVariable int id) throws IOException {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            if (notice.getAttachmentUrl() == null || notice.getAttachmentUrl().isEmpty()) {
                throw new IllegalArgumentException("다운로드할 첨부 파일이 없습니다.");
            }

            String key = notice.getAttachmentUrl().replace("https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            String originalFilename = notice.getAttachmentName();
            String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            String contentType = s3Object.response().contentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .body(new InputStreamResource(s3Object));
        } catch (Exception e) {
            log.error("첨부 파일 다운로드 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete-image")
    @ResponseBody
    public Map<String, Object> deleteImage(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            noticeService.deleteImage(id);
            response.put("success", true);
            log.info("이미지 삭제 성공 - ID: {}", id);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            log.error("이미지 삭제 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
        }
        return response;
    }

    @PostMapping("/{id}/delete-attachment")
    @ResponseBody
    public Map<String, Object> deleteAttachment(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            noticeService.deleteAttachment(id);
            response.put("success", true);
            log.info("첨부 파일 삭제 성공 - ID: {}", id);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            log.error("첨부 파일 삭제 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
        }
        return response;
    }

    private int extractUserId(Object principal) {
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
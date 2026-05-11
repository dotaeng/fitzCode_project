package kr.co.fitzcode.common.controller;

import kr.co.fitzcode.admin.service.NoticeService;
import kr.co.fitzcode.common.dto.NoticeDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
@Slf4j
public class UserNoticeController {

    private final NoticeService noticeService;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    // 공지사항 리스트
    @GetMapping
    public String getAllNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            int pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }

            List<NoticeDTO> notices = noticeService.getNoticesWithPagination(pageIndex, size);
            long totalNotices = noticeService.getTotalNoticeCount();
            int totalPages = (int) Math.ceil((double) totalNotices / size);
            if (totalPages == 0) {
                totalPages = 1;
            }

            int startPage = Math.max(1, page - 2);
            int endPage = Math.min(totalPages, startPage + 4);
            if (endPage - startPage < 4) {
                startPage = Math.max(1, endPage - 4);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("notices", notices);
            model.addAttribute("currentPage", page);
            model.addAttribute("menuCurrentPage", "notice");
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("size", size);
            return "notice/list";
        } catch (Exception e) {
            log.error("공지사항 목록 조회 실패: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    // 공지사항 상세 페이지
    @GetMapping("/{id}")
    public String getNoticeById(@PathVariable int id, Model model) {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            model.addAttribute("notice", notice);
            model.addAttribute("menuCurrentPage", "notice");
            return "notice/detail";
        } catch (Exception e) {
            log.error("공지사항 상세 조회 실패 - ID: {}, 메시지: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    // 첨부 파일 다운로드
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

    // FAQ 페이지
    @GetMapping("/faq")
    public String getAllFaqs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            int totalFaqs = 12; // FAQ 개수
            int pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }

            int start = pageIndex * size;
            int end = Math.min(start + size, totalFaqs);
            int totalPages = (int) Math.ceil((double) totalFaqs / size);
            if (totalPages == 0) {
                totalPages = 1;
            }

            int startPage = Math.max(1, page - 2);
            int endPage = Math.min(totalPages, startPage + 4);
            if (endPage - startPage < 4) {
                startPage = Math.max(1, endPage - 4);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());

            // 더미 리스트
            List<FaqDTO> faqs = new ArrayList<>();
            for (int i = start; i < end; i++) {
                FaqDTO faq = new FaqDTO();
                faq.setFaqId(i + 1);
                faq.setTitle(getFaqTitle(i + 1));
                faq.setContent(getFaqContent(i + 1));
                faqs.add(faq);
            }

            model.addAttribute("faqs", faqs);
            model.addAttribute("currentPage", page);
            model.addAttribute("menuCurrentPage", "faq"); // FAQ 페이지이므로 "faq"로 설정
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("size", size);
            return "notice/faq";
        } catch (Exception e) {
            log.error("FAQ 목록 조회 실패: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "404-error";
        }
    }

    // 더미
    @Data
    private static class FaqDTO {
        private int faqId;
        private String title;
        private String content;

        public FaqDTO() {
        }

        public FaqDTO(int faqId) {
            this.faqId = faqId;
        }
    }

    // FAQ 제목
    private String getFaqTitle(int id) {
        switch (id) {
            case 1: return "배송은 얼마나 걸리나요?";
            case 2: return "환불 정책은 어떻게 되나요?";
            case 3: return "회원 가입은 어떻게 하나요?";
            case 4: return "비밀번호를 잊어버렸어요.";
            case 5: return "주문 상태는 어디서 확인하나요?";
            case 6: return "배송비는 얼마인가요?";
            case 7: return "결제 수단은 어떤 것들이 있나요?";
            case 8: return "상품 교환은 어떻게 하나요?";
            case 9: return "포인트 적립은 어떻게 되나요?";
            case 10: return "고객센터 연락처는 무엇인가요?";
            case 11: return "FAQ 테스트 데이터 11";
            case 12: return "FAQ 테스트 데이터 12";
            default: return "제목 없음";
        }
    }

    // FAQ 내용
    private String getFaqContent(int id) {
        switch (id) {
            case 1: return "배송은 보통 3~5일 소요됩니다.";
            case 2: return "구매 후 7일 이내에 환불 요청이 가능합니다.";
            case 3: return "홈페이지 상단의 회원가입 버튼을 클릭하세요.";
            case 4: return "로그인 페이지에서 비밀번호 찾기를 이용하세요.";
            case 5: return "마이페이지에서 주문 상태를 확인할 수 있습니다.";
            case 6: return "배송비는 3,000원이며, 5만 원 이상 구매 시 무료입니다.";
            case 7: return "신용카드, 계좌이체, 간편 결제를 지원합니다.";
            case 8: return "마이페이지에서 교환 신청을 진행하세요.";
            case 9: return "구매 금액의 1%가 포인트로 적립됩니다.";
            case 10: return "고객센터 번호는 1234-5678입니다.";
            case 11: return "테스트 데이터 내용 11입니다.";
            case 12: return "테스트 데이터 내용 12입니다.";
            default: return "내용 없음";
        }
    }
}
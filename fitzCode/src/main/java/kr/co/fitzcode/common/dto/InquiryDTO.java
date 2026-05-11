package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.InquiryCategory;
import kr.co.fitzcode.common.enums.InquiryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "문의 정보")
public class InquiryDTO {
    @Schema(description = "문의 ID")
    private int inquiryId;

    @Schema(description = "문의한 사용자 ID")
    private int userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "사용자 핸드폰 번호")
    private String phoneNumber;

    @Schema(description = "관련 주문 ID")
    private Integer orderId;

    @Schema(description = "문의 제목")
    private String subject;

    @Schema(description = "문의 내용")
    private String content;

    @Schema(description = "문의 카테고리 코드")
    private Integer categoryCode;

    @Schema(description = "문의 상태 코드")
    private int statusCode;

    @Schema(description = "관리자 답변 내용")
    private String reply;

    @Schema(description = "문의 작성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "문의 수정 날짜")
    private LocalDateTime updatedAt;

    @Schema(description = "관련 상품 ID")
    private Integer productId;

    @Schema(description = "상품 이름")
    private String productName;

    @Schema(description = "상품 대표 이미지 URL")
    private String productImgUrl;

    @Schema(description = "상품 브랜드")
    private String brand;

    @Schema(description = "상품 상세설명")
    private String description;

    @Schema(description = "개별 상품 가격")
    private String price;

    @Schema(description = "문의 시 첨부한 이미지")
    private List<String> imageUrls;

    public InquiryStatus getStatus() {
        return InquiryStatus.fromCode(statusCode);
    }

    public InquiryCategory getCategory() {
        return InquiryCategory.fromCode(categoryCode);
    }
}
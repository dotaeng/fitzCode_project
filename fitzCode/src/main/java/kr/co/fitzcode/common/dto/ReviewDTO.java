package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Schema(description = "리뷰 정보")
public class ReviewDTO {
    @Schema(description = "리뷰 ID")
    private Long reviewId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "평점")
    private Integer rating;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "생성 날짜")
    private Timestamp createdAt;

    @Schema(description = "수정 날짜")
    private Timestamp updatedAt;

    @Schema(description = "이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "포맷팅된 생성 날짜 (yy.MM.dd)")
    private String createdAtStr;
}
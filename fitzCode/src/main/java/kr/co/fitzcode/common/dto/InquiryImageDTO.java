package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "문의 이미지 정보")
public class InquiryImageDTO {
    @Schema(description = "이미지 ID")
    private int imageId;

    @Schema(description = "문의 ID")
    private int inquiryId;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "이미지 순서")
    private int imageOrder;

    @Schema(description = "이미지 생성날짜")
    private LocalDateTime createdAt;
}
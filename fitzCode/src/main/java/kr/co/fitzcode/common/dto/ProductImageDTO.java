package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "상품 이미지 정보")
public class ProductImageDTO {
    @Schema(description = "상품 이미지 ID")
    private Long productImageId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "이미지 순서")
    private Integer imageOrder;
}
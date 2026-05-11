package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주목받는 상품 정보")
public class PickProductDTO {
    @Schema(description = "픽 ID")
    private Long pickId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "표시 순서 (1~10)")
    private Integer displayOrder;

    @Schema(description = "상품 세부 정보")
    private ProductDTO product;
}
package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.ProductSize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "상품 사이즈 정보")
public class ProductSizeDTO {
    @Schema(description = "상품 사이즈 ID")
    private Long productSizeId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "사이즈 코드")
    private Integer sizeCode;

    @Schema(description = "재고")
    private Integer stock;

    public String getSizeDescription() {
        if (sizeCode == null) return "알 수 없음";
        try {
            return ProductSize.fromCode(sizeCode).getDescription();
        } catch (IllegalArgumentException e) {
            return "알 수 없음";
        }
    }

    public String getSizeName() {
        return ProductSize.fromCode(this.sizeCode).getDescription();
    }
}
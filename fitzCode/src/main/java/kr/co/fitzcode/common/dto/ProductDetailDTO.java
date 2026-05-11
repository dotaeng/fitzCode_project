package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "상품 상세 정보")
public class ProductDetailDTO {
    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "상품 설명")
    private String description;

    @Schema(description = "브랜드")
    private String brand;

    @Schema(description = "가격")
    private int price;

    @Schema(description = "재고")
    private int stock;

    @Schema(description = "상태 코드")
    private int status;

    @Schema(description = "카테고리 이름")
    private String categoryName;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "카테고리 ID")
    private Integer categoryId;

    @Schema(description = "할인 가격")
    private Integer discountedPrice;

    @Schema(description = "사이즈 목록")
    private List<ProductSizeDTO> sizes = new ArrayList<>();

    @Schema(description = "이미지 목록")
    private List<ProductImageDTO> images = new ArrayList<>();

    @Schema(description = "모든 사이즈 목록")
    private List<ProductSizeDTO> allSizes = new ArrayList<>();

    public String getStatusDesc() {
        return ProductStatus.fromCode(this.status).getDescription();
    }

    public Integer getDiscountRate() {
        if (discountedPrice == null || discountedPrice <= 0) {
            return 0;
        }
        if (price <= 0) {
            return 0;
        }
        int diff = price - discountedPrice;
        double rate = (diff / (double) price) * 100.0;
        return (int) Math.round(rate);
    }
}
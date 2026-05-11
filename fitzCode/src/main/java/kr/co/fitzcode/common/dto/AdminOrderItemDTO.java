package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "관리자 주문 상품 정보")
public class AdminOrderItemDTO {
    @Schema(description = "주문 상세 ID")
    private Long orderDetailId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "상품 설명")
    private String description;

    @Schema(description = "주문 수량")
    private Integer quantity;

    @Schema(description = "개별 상품 가격")
    private Double unitPrice;

    @Schema(description = "소계")
    private Double subtotal;

    @Schema(description = "상품 이미지")
    private String imageUrl;
}
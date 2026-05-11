package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답 정보")
public class ProductResponseDTO {
    private static final int PRODUCT_PER_PAGE = 20;

    @Schema(description = "상품 목록")
    private List<ProductDTO> list;

    @Schema(description = "총 상품 수")
    private int totalLength;

    @Schema(description = "총 페이지 수")
    private int totalPages;

    @Schema(description = "현재 페이지")
    private int currentPage;

    @Schema(description = "페이지당 상품 수")
    private int pageSize;
}
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
@Schema(description = "판매 순위 정보")
public class SalesRankingDTO {
    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "총 판매액")
    private int totalSales;

    @Schema(description = "판매 수량")
    private int quantitySold;

    @Schema(description = "이미지 URL")
    private String imageUrl;
}
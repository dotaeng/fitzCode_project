package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 정보")
public class CartDTO {
    @Schema(description = "장바구니 ID")
    private int cartId;

    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "상품 ID")
    private int productId;

    @Schema(description = "수량")
    private int quantity;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "상품 사이즈 ID")
    private int productSizeId;


    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(formatter);
        }
        return "";
    }

}
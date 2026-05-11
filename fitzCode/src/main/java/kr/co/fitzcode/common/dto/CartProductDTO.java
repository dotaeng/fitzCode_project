package kr.co.fitzcode.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProductDTO {
    // information from cart table
    private int cartId;
    private int userId;
    private int productId;
    private int quantity;
    private LocalDateTime createdAt;
    private int sizeCode;
    private int stock;

    // information from product table
    private String productName;
    private String description;
    private int price;
    private Integer discountedPrice;
    private String imageUrl;

    // sizes
    private List<ProductSizeDTO> productSizes; // 사이즈별 재고




    public String getFormattedDiscountedPrice() {
        if (this.discountedPrice != null && this.discountedPrice instanceof Number) {
            return new DecimalFormat("#,###").format(this.discountedPrice);
        }
        return "N/A"; // or handle this case differently
    }

    public String getFormattedDiscountPercentage() {
        if (price != 0) {
            double discountPercentage = ((double)(price - discountedPrice) / price) * 100;
            DecimalFormat df = new DecimalFormat("#");
            return df.format(discountPercentage);
        }
        return "0";
    }


    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(formatter);
        }
        return "";
    }

}

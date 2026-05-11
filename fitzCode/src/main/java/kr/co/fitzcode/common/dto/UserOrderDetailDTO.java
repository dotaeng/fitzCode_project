package kr.co.fitzcode.common.dto;

import kr.co.fitzcode.common.enums.OrderStatus;
import kr.co.fitzcode.common.enums.ProductSize;
import kr.co.fitzcode.common.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderDetailDTO {

    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private int price;
    private int couponId;
    private int refundStatus;
    private int sizeCode;
    private String imageUrl;
    private String productName;
    private LocalDateTime createdAt;

    public String getFormattedRefundStatus() {
        return RefundStatus.fromCode(refundStatus).getDescription();
    }

    public String getFormattedSizeCode() {
        return ProductSize.fromCode(sizeCode).getDescription();
    }

    public String getFormattedPrice() {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }



    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(formatter);
        }
        return "";
    }

}

package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.OrderStatus;
import kr.co.fitzcode.common.enums.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderDTO {

    @Schema(description = "주문번호")
    private int orderId;

    @Schema(description = "사용자 아이디")
    private int userId;

    private int addressId;

    @Schema(description = "총 주문 금액")
    private Integer totalPrice;

    @Schema(description = "주문 상태 코드")
    private int orderStatus;


    @Schema(description = "주문일")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public String getFormattedTotalPrice() {
        if (totalPrice != null) {
            DecimalFormat df = new DecimalFormat("#,###원");
            return df.format(totalPrice);
        } else {
            return "N/A";
        }
    }

    public String getOrderStatusTranslated() {
        return OrderStatus.fromCode(this.orderStatus).getDescription();
    }


    public String getFormattedCreatedAt(){
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(formatter);
        }
        return "";
    }


}

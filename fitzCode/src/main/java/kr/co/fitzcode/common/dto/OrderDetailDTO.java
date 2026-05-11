package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "주문 상세 정보")
public class OrderDetailDTO {
    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "주문 날짜")
    private LocalDateTime orderDate;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "수량")
    private Integer quantity;

    @Schema(description = "총 금액")
    private Integer totalAmount;

    @Schema(description = "주문 상태")
    private String orderStatus;

    public String getOrderStatusInKorean() {
        switch (orderStatus) {
            case "PLACED": return "주문 접수";
            case "SHIPPED": return "배송 중";
            case "DELIVERED": return "배송 완료";
            case "CANCELLED": return "주문 취소";
            case "UNKNOWN": return "알 수 없음";
            default: return "알 수 없음";
        }
    }
}
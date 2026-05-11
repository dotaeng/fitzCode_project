package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "관리자 주문 정보")
public class AdminOrderDTO {
    @Schema(description = "주문번호")
    private Long orderId;

    @Schema(description = "주문상태")
    private OrderStatus orderStatus;

    @Schema(description = "대표 상품명")
    private String productName;

    @Schema(description = "주문날짜")
    private LocalDateTime createdAt;
}
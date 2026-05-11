package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "관리자 주문 상세 정보")
public class AdminOrderDetailDTO {
    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "주문 상태")
    private Integer orderStatus;

    @Schema(description = "주문 생성일")
    private LocalDateTime createdAt;

    @Schema(description = "총 주문 금액")
    private Double totalAmount;

    @Schema(description = "주문자 ID")
    private Long userId;

    @Schema(description = "주문자 이름")
    private String username;

    @Schema(description = "주문자 이메일")
    private String email;

    @Schema(description = "주문 상품 목록")
    private List<AdminOrderItemDTO> items;

    @Schema(description = "배송 상태")
    private Integer deliveryStatus;

    @Schema(description = "운송장 번호")
    private String trackingNumber;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송 시작일")
    private LocalDateTime shippedAt;

    @Schema(description = "배송 완료일")
    private LocalDateTime deliveredAt;

    @Schema(description = "결제 수단")
    private Integer paymentMethod;

    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "계좌 번호")
    private String accountNumber;

    @Schema(description = "예금주")
    private String accountHolder;
}
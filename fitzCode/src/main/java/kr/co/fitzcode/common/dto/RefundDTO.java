package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.OrderStatus;
import kr.co.fitzcode.common.enums.RefundStatus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "환불 정보")
public class RefundDTO {
    private static final Logger log = LoggerFactory.getLogger(RefundDTO.class);

    @Schema(description = "환불 ID")
    private Long refundId;

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "주문 상세 ID")
    private Long orderDetailId;

    @Schema(description = "결제 ID")
    private Long paymentId;

    @Schema(description = "환불 사유")
    private String refundReason;

    @Schema(description = "환불 상태")
    private RefundStatus refundStatus;

    @Schema(description = "환불 금액")
    private Integer refundAmount;

    @Schema(description = "남은 환불 가능 금액")
    private Integer remainingRefundAmount;

    @Schema(description = "환불 방법")
    private Integer refundMethod;

    @Schema(description = "요청 날짜")
    private LocalDateTime requestedAt;

    @Schema(description = "처리 날짜")
    private LocalDateTime processedAt;

    @Schema(description = "완료 날짜")
    private LocalDateTime completedAt;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "거래 ID")
    private String transactionId;

    @Schema(description = "결제 금액")
    private Integer paymentAmount;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "요청된 항목")
    private List<OrderDetailDTO> requestedItems;

    @Schema(description = "계좌주")
    private String accountHolder;

    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "계좌 번호")
    private String accountNumber;

    @Schema(description = "환불 상태 코드")
    private Integer refundStatusCode;

    public Integer getCalculatedRefundAmount() {
        if (requestedItems == null || requestedItems.isEmpty()) {
            log.debug("No requested items found or empty for refundId: {}", refundId);
            return 0;
        }
        int calculatedAmount = requestedItems.stream()
                .mapToInt(item -> {
                    int amount = (item.getRefundAmount() != null ? item.getRefundAmount() : item.getPrice() * item.getQuantity());
                    log.debug("Item - orderDetailId: {}, price: {}, quantity: {}, amount: {}",
                            item.getOrderDetailId(), item.getPrice(), item.getQuantity(), amount);
                    return amount > 0 ? amount : 0;
                })
                .sum();
        log.debug("Calculated refund amount before limit for refundId {}: {}", refundId, calculatedAmount);
        int limitedAmount = Math.min(calculatedAmount, this.paymentAmount != null ? this.paymentAmount : Integer.MAX_VALUE);
        log.debug("Limited refund amount for refundId {}: {}", refundId, limitedAmount);
        return limitedAmount;
    }

    public Integer getRequestedRefundAmount() {
        if (requestedItems == null || requestedItems.isEmpty()) {
            log.debug("No requested items for requested refund amount calculation, refundId: {}", refundId);
            return 0;
        }
        int requestedAmount = requestedItems.stream()
                .mapToInt(item -> {
                    int price = item.getPrice() != null ? item.getPrice() : 0;
                    int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    int amount = price * quantity;
                    log.debug("Requested item - orderDetailId: {}, price: {}, quantity: {}, amount: {}",
                            item.getOrderDetailId(), price, quantity, amount);
                    return amount > 0 ? amount : 0;
                })
                .sum();
        log.debug("Total requested refund amount for refundId {}: {}", refundId, requestedAmount);
        return requestedAmount;
    }

    public Integer getRefundStatusCode() {
        return refundStatus != null ? refundStatus.getCode() : null;
    }

    public void setRefundStatusCode(Integer code) {
        this.refundStatusCode = code;
        this.refundStatus = code != null ? RefundStatus.fromCode(code) : null;
    }

    public void setRefundStatusByCode(Integer code) {
        this.refundStatus = code != null ? RefundStatus.fromCode(code) : null;
    }

    public String getRefundStatusInKorean() {
        return refundStatus != null ? refundStatus.getDescription() : "알 수 없음";
    }

    @Data
    @Schema(description = "환불 주문 상세 정보")
    public static class OrderDetailDTO {
        private static final Logger log = LoggerFactory.getLogger(OrderDetailDTO.class);

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
        private OrderStatus orderStatus;

        @Schema(description = "주문 상세 ID")
        private Long orderDetailId;

        @Schema(description = "상품 ID")
        private Long productId;

        @Schema(description = "상품명")
        private String productName;

        @Schema(description = "가격")
        private Integer price;

        @Schema(description = "환불 상태")
        private RefundStatus refundStatus;

        @Schema(description = "환불 금액")
        private Integer refundAmount;

        @Schema(description = "환불 상태 코드")
        private Integer refundStatusCode;

        public Integer getOrderStatusCode() {
            return orderStatus != null ? orderStatus.getCode() : null;
        }

        public void setOrderStatusByCode(Integer code) {
            this.orderStatus = code != null ? OrderStatus.fromCode(code) : null;
        }

        public Integer getRefundStatusCode() {
            return refundStatus != null ? refundStatus.getCode() : null;
        }

        public void setRefundStatusCode(Integer code) {
            this.refundStatusCode = code;
            try {
                this.refundStatus = code != null ? RefundStatus.fromCode(code) : null;
            } catch (IllegalArgumentException e) {
                this.refundStatus = null;
                log.warn("Invalid refund status code: {} for orderDetailId: {}", code, orderDetailId, e);
            }
        }

        public void setRefundStatusByCode(Integer code) {
            this.refundStatus = code != null ? RefundStatus.fromCode(code) : null;
        }

        public String getOrderStatusInKorean() {
            return orderStatus != null ? orderStatus.getDescription() : "알 수 없음";
        }

        public String getRefundStatusInKorean() {
            return refundStatus != null ? refundStatus.getDescription() : "환불 없음";
        }
    }
}
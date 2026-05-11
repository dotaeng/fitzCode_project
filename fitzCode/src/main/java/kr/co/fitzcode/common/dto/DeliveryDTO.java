package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "배송 정보")
public class DeliveryDTO {
    @Schema(description = "배송 ID")
    private Long deliveryId;

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "운송장 번호")
    private String trackingNumber;

    @Schema(description = "택배사 코드")
    private Integer courierCode;

    @Schema(description = "배송 상태")
    private DeliveryStatus deliveryStatus;

    @Schema(description = "배송 시작일")
    private LocalDateTime shippedAt;

    @Schema(description = "배송 완료일")
    private LocalDateTime deliveredAt;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public DeliveryDTO() {
        this.courierCode = 1; // CJ대한통운 고정
    }

    public void setCourierCode(Integer courierCode) {
        // 고정값 유지
    }
}
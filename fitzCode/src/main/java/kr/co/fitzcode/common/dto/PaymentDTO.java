package kr.co.fitzcode.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private int paymentId;
    private int orderId;
    private int paymentMethod;
    private int paymentStatus;
    private int amount;
    private LocalDateTime paymentDate;
    private String transactionId;

}

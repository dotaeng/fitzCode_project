package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.PaymentDTO;

public interface PaymentService {
    void createPayment(PaymentDTO paymentDTO);
}

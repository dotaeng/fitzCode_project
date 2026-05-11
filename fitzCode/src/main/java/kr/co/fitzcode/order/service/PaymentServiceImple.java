package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.PaymentDTO;
import kr.co.fitzcode.order.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImple implements PaymentService {
    private final PaymentMapper paymentMapper;

    @Override
    public void createPayment(PaymentDTO paymentDTO) {
        paymentMapper.createPayment(paymentDTO);
    }
}

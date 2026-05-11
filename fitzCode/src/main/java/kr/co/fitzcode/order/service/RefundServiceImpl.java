package kr.co.fitzcode.order.service;

import kr.co.fitzcode.order.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundMapper refundMapper;

    @Override
    public void requestRefund(int orderDetailId) {
        refundMapper.requestRefund(orderDetailId);
    }
}

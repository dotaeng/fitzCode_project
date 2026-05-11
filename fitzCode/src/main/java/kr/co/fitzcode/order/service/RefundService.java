package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.RefundDTO;

public interface RefundService {
    void requestRefund(int orderDetailId);
}

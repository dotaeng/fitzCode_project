package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.UserOrderDetailDTO;

import java.util.List;
import java.util.Map;

public interface UserOrderDetailService {
    void addOrderDetailToOrder(List<Map<String, Object>> batchInsertList);

    List<UserOrderDetailDTO> getOrderDetailByOrderId(int orderId);
    List<UserOrderDetailDTO> getOrderDetailByUserId(int userId);

    void updateRequestRefundStatus(int orderDetailId);

    void decrementProductCount(int productId, int sizeCode);
}


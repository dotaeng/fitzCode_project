package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.UserOrderDetailDTO;
import kr.co.fitzcode.order.mapper.UserOrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserOrderDetailServiceImple implements UserOrderDetailService {

    private final UserOrderDetailMapper userOrderDetailMapper;


    @Override
    public void addOrderDetailToOrder(List<Map<String, Object>> batchInsertList) {

        userOrderDetailMapper.batchInsertOrderDetails(batchInsertList);
    }

    @Override
    public List<UserOrderDetailDTO> getOrderDetailByOrderId(int orderId) {
        return userOrderDetailMapper.getOrderDetailByOrderId(orderId);
    }

    @Override
    public List<UserOrderDetailDTO> getOrderDetailByUserId(int userId) {
        return userOrderDetailMapper.getOrderDetailByUserId(userId);
    }

    @Override
    public void updateRequestRefundStatus(int orderDetailId) {
        userOrderDetailMapper.updateRequestRefundStatus(orderDetailId);
    }

    @Override
    public void decrementProductCount(int productId, int sizeCode) {
        userOrderDetailMapper.decrementProductCountInProductSize(productId, sizeCode);
        userOrderDetailMapper.decrementProductCountInProduct(productId);
    }
}

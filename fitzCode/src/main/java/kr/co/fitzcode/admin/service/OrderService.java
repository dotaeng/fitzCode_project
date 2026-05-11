package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.OrderMapper;
import kr.co.fitzcode.common.dto.OrderDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;

    public List<OrderDetailDTO> getOrderDetailsByProductIdWithPagination(Long productId, int offset, int limit) {
        return orderMapper.findOrderDetailsByProductIdWithPagination(productId, offset, limit);
    }

    public int countOrderDetailsByProductId(Long productId) {
        return orderMapper.countOrderDetailsByProductId(productId);
    }
}
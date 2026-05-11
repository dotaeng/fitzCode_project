package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.AdminOrderMapper;
import kr.co.fitzcode.common.dto.AdminOrderDTO;
import kr.co.fitzcode.common.dto.AdminOrderDetailDTO;
import kr.co.fitzcode.common.dto.DeliveryDTO;
import kr.co.fitzcode.common.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final AdminOrderMapper orderMapper;

    private static final int PAGE_SIZE = 10;
    private static final int PAGE_BUTTON_COUNT = 5;

    // 주문 목록 조회 (페이지네이션)
    @Override
    public List<AdminOrderDTO> getOrderList(int page, int size, Integer status, String sortBy) {
        if (page < 1) page = 1;
        if (size != PAGE_SIZE) size = PAGE_SIZE;
        int offset = (page - 1) * size;
        return orderMapper.getOrderList(size, offset, status, sortBy);
    }

    // 전체 주문 개수 조회 (필터)
    @Override
    public int getTotalOrderCount(Integer status) {
        return orderMapper.getTotalOrderCount(status);
    }

    // 페이지 크기가 기본 크기와 다르면 기본 크기로 설정
    @Override
    public int calculateTotalPages(int totalCount, int size) {
        if (size != PAGE_SIZE) size = PAGE_SIZE;
        return Math.max(1, (int) Math.ceil((double) totalCount / size));
    }

    // 페이지네이션 시작 페이지
    @Override
    public int[] getPageRange(int currentPage, int totalPages) {
        int startPage = Math.max(1, currentPage - (PAGE_BUTTON_COUNT / 2));
        int endPage = Math.min(totalPages, startPage + PAGE_BUTTON_COUNT - 1);
        startPage = Math.max(1, endPage - PAGE_BUTTON_COUNT + 1);

        int[] range = new int[endPage - startPage + 1];
        for (int i = 0; i < range.length; i++) {
            range[i] = startPage + i;
        }
        return range;
    }

    // 주문 상세 정보 조회
    @Override
    public AdminOrderDetailDTO getOrderDetail(Long orderId) {
        return orderMapper.getOrderDetail(orderId);
    }

    // 주문 상태 업데이트
    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        orderMapper.updateOrderStatus(orderId, status);
    }

    // 특정 주문 ID에 대한 배송 정보 조회
    @Override
    public DeliveryDTO getDeliveryByOrderId(Long orderId) {
        return orderMapper.getDeliveryByOrderId(orderId);
    }

    // 배송 정보 업데이트
    @Override
    public void updateDelivery(DeliveryDTO delivery) {
        DeliveryDTO existing = orderMapper.getDeliveryByOrderId(delivery.getOrderId());
        DeliveryDTO updatedDelivery = existing != null ? existing : new DeliveryDTO();
        updatedDelivery.setOrderId(delivery.getOrderId());
        updatedDelivery.setTrackingNumber(delivery.getTrackingNumber());
        updatedDelivery.setDeliveryStatus(delivery.getDeliveryStatus() != null ? delivery.getDeliveryStatus() : DeliveryStatus.IN_TRANSIT);
        updatedDelivery.setShippedAt(delivery.getShippedAt() != null ? delivery.getShippedAt() : LocalDateTime.now());
        updatedDelivery.setDeliveredAt(delivery.getDeliveredAt() != null ? delivery.getDeliveredAt() : updatedDelivery.getDeliveredAt());
        if (existing == null) {
            updatedDelivery.setCreatedAt(LocalDateTime.now());
            orderMapper.insertDelivery(updatedDelivery);
        } else {
            orderMapper.updateDelivery(updatedDelivery);
        }
    }

    // 배송중 상태의 모든 배송 정보 조회
    @Override
    public List<DeliveryDTO> getAllDeliveriesInShipping() {
        return orderMapper.getAllDeliveriesInShipping();
    }
}
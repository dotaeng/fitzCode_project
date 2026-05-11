package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.AdminOrderDTO;
import kr.co.fitzcode.common.dto.AdminOrderDetailDTO;
import kr.co.fitzcode.common.dto.DeliveryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminOrderMapper {

    // 주문 목록 조회 (페이징 및 상태 필터링 지원)
    List<AdminOrderDTO> getOrderList(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("status") Integer status,
            @Param("sortBy") String sortBy);

    // 특정 상태에 해당하는 총 주문 개수 조회
    int getTotalOrderCount(@Param("status") Integer status);

    // 특정 주문 ID에 대한 상세 정보 조회
    AdminOrderDetailDTO getOrderDetail(@Param("orderId") Long orderId);

    // 주문 상태 업데이트
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    // 특정 주문 ID에 해당하는 배송 정보 조회
    DeliveryDTO getDeliveryByOrderId(@Param("orderId") Long orderId);

    // 새로운 배송 정보 삽입
    void insertDelivery(DeliveryDTO delivery);

    // 기존 배송 정보 업데이트
    void updateDelivery(DeliveryDTO delivery);

    // 배송중 상태의 모든 배송 정보 조회
    List<DeliveryDTO> getAllDeliveriesInShipping();
}
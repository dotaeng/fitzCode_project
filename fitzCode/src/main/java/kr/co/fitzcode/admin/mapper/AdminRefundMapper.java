package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.RefundDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRefundMapper {

    // 환불 목록
    List<RefundDTO> getRefundList(
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("status") Integer status,
            @Param("orderId") String orderId
    );

    // 특정 환불 ID에 해당하는 환불 상세 정보를 조회
    RefundDTO getRefundDetail(@Param("refundId") Long refundId);

    // 환불 상태를 업데이트
    void updateRefundStatus(@Param("refundId") Long refundId, @Param("status") int status);

    // 남은 환불 가능 금액을 업데이트
    void updateRemainingRefundAmount(@Param("refundId") Long refundId, @Param("remainingAmount") int remainingAmount);

    // 주문 상세 항목의 환불 상태를 업데이트
    void updateOrderDetailRefundStatus(@Param("orderDetailId") Long orderDetailId, @Param("status") int status);

    // 환불 트랜잭션을 SALES_TRANSACTION_LOG 테이블에 삽입
    void insertSalesTransactionLog(
            @Param("orderId") Long orderId,
            @Param("orderDetailId") Long orderDetailId,
            @Param("userId") Long userId,
            @Param("amount") Long amount,
            @Param("transactionType") int transactionType
    );

    // 사용자에게 알림을 NOTIFICATION 테이블에 삽입
    void insertNotification(
            @Param("userId") Long userId,
            @Param("type") int type,
            @Param("message") String message,
            @Param("relatedId") Long relatedId
    );

    // 조건에 맞는 환불 건수를 조회
    int getRefundCount(@Param("status") Integer status, @Param("orderId") String orderId);

    // 환불 금액을 업데이트
    void updateRefundAmount(@Param("refundId") Long refundId, @Param("refundAmount") int refundAmount);

    // 특정 주문 ID에 해당하는 환불 요청 항목 목록을 조회
    List<RefundDTO.OrderDetailDTO> getRequestedRefundItems(@Param("orderId") Long orderId);

    // 새로운 환불 기록을 REFUND 테이블에 삽입
    void insertRefund(@Param("refund") RefundDTO refund);

    // 주문 ID를 기준으로 환불 상세 정보를 조회
    RefundDTO getRefundDetailByOrderId(@Param("orderId") Long orderId);

    // 주문 ID를 기준으로 결제 금액을 조회
    Integer getPaymentAmountByOrderId(@Param("orderId") Long orderId);
}
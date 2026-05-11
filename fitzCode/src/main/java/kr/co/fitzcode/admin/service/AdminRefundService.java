package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.common.dto.RefundDTO;
import kr.co.fitzcode.common.enums.RefundStatus;

import java.util.List;

public interface AdminRefundService {

    // 환불 목록을 페이지 단위로 조회
    List<RefundDTO> getRefundList(int page, int size, Integer status, String orderId);

    // 특정 환불 ID에 해당하는 환불 상세 정보를 조회
    RefundDTO getRefundDetail(Long refundId);

    // 환불 상태 업데이트
    // 환불 금액과 남은 환불 가능 금액도 함께 업데이트하며, 상태 변경에 따라 알림 및 트랜잭션 로그를 기록
    // TODO : 알림 구현
    void updateRefundStatus(Long refundId, RefundStatus status, Integer refundAmount);

    // 남은 환불 가능 금액 업데이트
    void updateRemainingRefundAmount(Long refundId, int remainingAmount);

    // 주문 상세 항목의 환불 상태 업데이트
    void updateOrderDetailRefundStatus(Long orderDetailId, int status);

    // 환불 트랜잭션을 로그로 기록
    void logTransaction(RefundDTO refund);

    // 사용자에게 환불 관련 알림 전송
    void sendNotification(RefundDTO refund, String message);

    // 조건에 맞는 환불 건수 조회
    int getRefundCount(Integer status, String orderId);

    // 새로운 환불 요청 생성
    void createRefund(Long orderId, String refundReason, Integer refundMethod);

    // 환불 금액 업데이트
    void updateRefundAmount(Long refundId, int refundAmount);
}
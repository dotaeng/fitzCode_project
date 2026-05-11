package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.AdminRefundMapper;
import kr.co.fitzcode.common.dto.RefundDTO;
import kr.co.fitzcode.common.enums.RefundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

    private final AdminRefundMapper refundMapper;
    private static final int PAGE_SIZE = 10;

    // 환불 목록 (페이징)
    @Override
    public List<RefundDTO> getRefundList(int page, int size, Integer status, String orderId) {
        if (page < 1) page = 1;
        if (size != PAGE_SIZE) size = PAGE_SIZE;
        int offset = (page - 1) * size;
        log.info("환불 목록 조회 - 페이지: {}, 개수: {}, 상태: {}, 주문 ID: {}", page, size, status, orderId);
        List<RefundDTO> refunds = refundMapper.getRefundList(size, offset, status, orderId);
        log.debug("조회된 환불 목록 개수: {}", refunds.size());

        for (RefundDTO refund : refunds) {
            List<RefundDTO.OrderDetailDTO> items = refundMapper.getRequestedRefundItems(refund.getOrderId());
            refund.setRequestedItems(items);
            log.debug("환불 ID: {}, 요청된 항목 개수: {}, 요청 환불 금액: {}",
                    refund.getRefundId(), items != null ? items.size() : 0, refund.getRequestedRefundAmount());
        }
        return refunds;
    }

    // 특정 환불 ID에 해당하는 환불 상세 정보 조회
    @Override
    public RefundDTO getRefundDetail(Long refundId) {
        log.info("환불 상세 조회 - 환불 ID: {}", refundId);
        RefundDTO refund = refundMapper.getRefundDetail(refundId);
        if (refund == null) {
            log.warn("환불 기록 없음 - 환불 ID: {}", refundId);
            throw new RuntimeException("환불 기록을 찾을 수 없음");
        }
        log.debug("조회된 환불 정보 - 환불 ID: {}, 주문 ID: {}, 결제 금액: {}, 환불 금액: {}, 남은 환불 가능 금액: {}",
                refund.getRefundId(), refund.getOrderId(), refund.getPaymentAmount(), refund.getRefundAmount(), refund.getRemainingRefundAmount());
        List<RefundDTO.OrderDetailDTO> items = refundMapper.getRequestedRefundItems(refund.getOrderId());
        refund.setRequestedItems(items);
        log.debug("요청된 항목 개수: {}, 항목: {}", items != null ? items.size() : 0, items);
        int calculatedAmount = refund.getCalculatedRefundAmount();
        log.debug("계산된 환불 금액: {}", calculatedAmount);
        if (refund.getRefundAmount() == null || refund.getRefundAmount() == 0) {
            int adjustedAmount = Math.min(calculatedAmount, refund.getPaymentAmount() != null ? refund.getPaymentAmount() : calculatedAmount);
            refund.setRefundAmount(adjustedAmount);
            log.debug("조정된 환불 금액: {}", adjustedAmount);
        } else {
            log.debug("DB에서 가져온 기존 환불 금액 사용: {}", refund.getRefundAmount());
        }
        int remainingAmount = refund.getRemainingRefundAmount() != null ? refund.getRemainingRefundAmount()
                : (refund.getPaymentAmount() - (refund.getRefundAmount() != null ? refund.getRefundAmount() : 0));
        if (remainingAmount < 0) {
            log.warn("남은 환불 가능 금액이 음수 - 0으로 재설정, 환불 ID: {}", refundId);
            remainingAmount = 0;
        }
        refundMapper.updateRemainingRefundAmount(refundId, remainingAmount);
        refund.setRemainingRefundAmount(remainingAmount);
        log.debug("업데이트된 남은 환불 가능 금액: {}", remainingAmount);
        return refund;
    }


    // 환불 상태를 업데이트
    // 환불 금액과 남은 환불 가능 금액도 같이 업데이트하고
    // 상태 변경에 따라 알림 및 트랜잭션 로그 기록
    @Override
    @Transactional
    public void updateRefundStatus(Long refundId, RefundStatus status, Integer refundAmount) {
        log.info("환불 상태 변경 - 환불 ID: {}, 상태: {}, 환불 금액: {}", refundId, status, refundAmount);
        RefundDTO refund = getRefundDetail(refundId);

        int effectiveRefundAmount = (refundAmount != null && refundAmount > 0) ? refundAmount : refund.getCalculatedRefundAmount();
        if (status != RefundStatus.REJECTED) {
            int remainingAmount = refund.getRemainingRefundAmount() != null ? refund.getRemainingRefundAmount() : 0;
            if (effectiveRefundAmount > remainingAmount) {
                throw new IllegalArgumentException("유효한 환불 금액을 입력하세요 (최대: " + remainingAmount + "원)");
            }
            int newRefundAmount = (refund.getRefundAmount() != null ? refund.getRefundAmount() : 0) + effectiveRefundAmount;
            updateRefundAmount(refundId, newRefundAmount);
            refund.setRefundAmount(newRefundAmount);

            int newRemainingAmount = remainingAmount - effectiveRefundAmount;
            if (newRemainingAmount < 0) {
                throw new IllegalStateException("남은 환불 금액이 0원보다 아래임");
            }
            refundMapper.updateRemainingRefundAmount(refundId, newRemainingAmount);
            refund.setRemainingRefundAmount(newRemainingAmount);
            log.debug("업데이트된 환불 금액: {}, 남은 환불 가능 금액: {}", newRefundAmount, newRemainingAmount);
        }

        if (status == RefundStatus.COMPLETED) {
            updateRequestedItemsStatus(refund, RefundStatus.COMPLETED.getCode());
            logTransaction(refund);
            sendNotification(refund, refund.getRequestedItems() != null && !refund.getRequestedItems().isEmpty() ? "부분 환불이 완료되었습니다" : "환불이 완료되었습니다");
        } else if (status == RefundStatus.REJECTED) {
            updateRequestedItemsStatus(refund, RefundStatus.REJECTED.getCode());
            sendNotification(refund, "환불이 거절되었습니다");
        } else if (status == RefundStatus.PROCESSING) {
            updateRequestedItemsStatus(refund, RefundStatus.PROCESSING.getCode());
        }

        refundMapper.updateRefundStatus(refundId, status.getCode());
        log.info("환불 상태 변경 완료 - 환불 ID: {}, 상태: {}", refundId, status);
    }

    // 새로운 환불 요청 생성
    @Override
    @Transactional
    public void createRefund(Long orderId, String refundReason, Integer refundMethod) {
        log.info("환불 신청 생성 - 주문 ID: {}, 사유: {}, 방식: {}", orderId, refundReason, refundMethod);
        RefundDTO existingRefund = refundMapper.getRefundDetailByOrderId(orderId);
        if (existingRefund != null) {
            throw new IllegalStateException("이미 환불 신청이 존재합니다: 환불 ID = " + existingRefund.getRefundId());
        }

        RefundDTO refund = new RefundDTO();
        refund.setOrderId(orderId);
        refund.setRefundReason(refundReason);
        refund.setRefundMethod(refundMethod);
        refund.setRequestedAt(LocalDateTime.now());
        refund.setRefundStatusCode(RefundStatus.REQUESTED.getCode());
        refund.setRefundStatus(RefundStatus.REQUESTED);

        List<RefundDTO.OrderDetailDTO> items = refundMapper.getRequestedRefundItems(orderId);
        refund.setRequestedItems(items);

        int requestedRefundAmount = refund.getRequestedRefundAmount();
        refund.setRefundAmount(requestedRefundAmount);
        log.debug("초기 환불 금액 설정: {}", requestedRefundAmount);

        Integer paymentAmount = refundMapper.getPaymentAmountByOrderId(orderId);
        refund.setPaymentAmount(paymentAmount != null ? paymentAmount : 0);
        log.debug("주문 ID {}의 결제 금액: {}", orderId, paymentAmount);

        int initialRemainingAmount = paymentAmount != null ? paymentAmount - requestedRefundAmount : 0;
        if (initialRemainingAmount < 0) {
            throw new IllegalStateException("결제 금액이 요청 환불 금액보다 작습니다: 결제 금액=" + paymentAmount + ", 요청 환불 금액=" + requestedRefundAmount);
        }
        refund.setRemainingRefundAmount(initialRemainingAmount);
        log.debug("초기 남은 환불 가능 금액: {}", initialRemainingAmount);

        refundMapper.insertRefund(refund);
        log.info("환불 신청 생성 완료 - 환불 ID: {}", refund.getRefundId());

        updateRequestedItemsStatus(refund, RefundStatus.REQUESTED.getCode());
    }

    // 요청된 항목의 환불 상태 업데이트
    private void updateRequestedItemsStatus(RefundDTO refund, int status) {
        if (refund.getRequestedItems() != null) {
            refund.getRequestedItems().forEach(item ->
                    refundMapper.updateOrderDetailRefundStatus(item.getOrderDetailId(), status)
            );
        }
    }

    // 남은 환불 가능 금액을 업데이트
    @Override
    public void updateRemainingRefundAmount(Long refundId, int remainingAmount) {
        if (remainingAmount < 0) {
            throw new IllegalStateException("남은 환불 금액이 0원보다 아래임");
        }
        refundMapper.updateRemainingRefundAmount(refundId, remainingAmount);
        log.info("남은 환불 가능 금액 업데이트 완료 - 환불 ID: {}, 남은 금액: {}", refundId, remainingAmount);
    }

    // 주문 상세 항목의 환불 상태 업데이트
    @Override
    public void updateOrderDetailRefundStatus(Long orderDetailId, int status) {
        refundMapper.updateOrderDetailRefundStatus(orderDetailId, status);
        log.info("주문 상세 환불 상태 변경 - 주문 상세 ID: {}, 새로운 상태: {}", orderDetailId, status);
    }

    // 환불 트랜잭션을 로그 기록
    @Override
    public void logTransaction(RefundDTO refund) {
        refundMapper.insertSalesTransactionLog(
                refund.getOrderId(),
                refund.getOrderDetailId(),
                refund.getUserId(),
                refund.getRefundAmount() != null ? refund.getRefundAmount().longValue() : null,
                2
        );
        log.info("환불 트랜잭션 기록 완료 - 환불 ID: {}", refund.getRefundId());
    }

    // 사용자에게 환불 관련 알림 전송
    @Override
    public void sendNotification(RefundDTO refund, String message) {
        refundMapper.insertNotification(
                refund.getUserId(),
                1,
                message,
                refund.getRefundId()
        );
        log.info("환불 알림 전송 완료 - 환불 ID: {}", refund.getRefundId());
    }

    // 조건에 맞는 환불 건수 조회
    @Override
    public int getRefundCount(Integer status, String orderId) {
        log.info("환불 건수 조회 - 상태: {}, 주문 ID: {}", status, orderId);
        return refundMapper.getRefundCount(status, orderId);
    }

    // 환불 금액 업데이트
    @Override
    public void updateRefundAmount(Long refundId, int refundAmount) {
        refundMapper.updateRefundAmount(refundId, refundAmount);
        log.info("환불 금액 업데이트 완료 - 환불 ID: {}, 금액: {}", refundId, refundAmount);
    }
}
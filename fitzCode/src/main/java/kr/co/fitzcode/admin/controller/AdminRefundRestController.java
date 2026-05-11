package kr.co.fitzcode.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.fitzcode.admin.service.AdminRefundService;
import kr.co.fitzcode.common.dto.RefundDTO;
import kr.co.fitzcode.common.enums.RefundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/products/refund/api")
@RequiredArgsConstructor
@Tag(name = "Refund API", description = "관리자 환불 처리 API 제공, 환불 상태 업데이트 및 결제 취소 요청")
public class AdminRefundRestController {

    private final AdminRefundService refundService;

    @Value("${portone.imp_key}")
    private String impKey;

    @Value("${portone.imp_secret}")
    private String impSecret;

    @Operation(summary = "환불 처리", description = "환불 ID로 환불 요청을 처리하고 상태를 업데이트")
    @PostMapping("/{refundId}/process")
    public ResponseEntity<Map<String, Object>> processRefund(
            @Parameter @PathVariable Long refundId,
            @Parameter @RequestParam(value = "customRefundAmount", required = false) Integer customRefundAmount,
            @Parameter @RequestParam("status") Integer status,
            @Parameter(hidden = true) @RequestParam Map<String, String> allParams) {

        Map<String, Object> response = new HashMap<>();
        RefundDTO refund = refundService.getRefundDetail(refundId);

        if (refund == null) {
            response.put("success", false);
            response.put("message", "환불 정보를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 체크박스 선택된 항목 처리
        Map<Long, Integer> selectedItems = new HashMap<>();
        int calculatedRefundAmount = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("item_"))
                .mapToInt(entry -> {
                    try {
                        Long orderDetailId = Long.valueOf(entry.getValue());
                        selectedItems.put(orderDetailId, 1);
                        for (RefundDTO.OrderDetailDTO item : refund.getRequestedItems()) {
                            if (item.getOrderDetailId().equals(orderDetailId)) {
                                return item.getPrice() * item.getQuantity();
                            }
                        }
                        return 0;
                    } catch (NumberFormatException e) {
                        log.warn("Invalid orderDetailId format: {}", entry.getValue());
                        return 0;
                    }
                })
                .sum();

        int effectiveRefundAmount = 0;
        if (!selectedItems.isEmpty()) {
            effectiveRefundAmount = (customRefundAmount != null && customRefundAmount > 0) ? customRefundAmount : calculatedRefundAmount;
            if (customRefundAmount != null && customRefundAmount != calculatedRefundAmount) {
                log.warn("커스텀 금액({})과 선택된 항목 합계({})가 일치하지 않습니다. 선택된 항목 합계를 우선 적용합니다.", customRefundAmount, calculatedRefundAmount);
                effectiveRefundAmount = calculatedRefundAmount;
            }
        } else if (customRefundAmount != null && customRefundAmount > 0) {
            effectiveRefundAmount = customRefundAmount;
        } else {
            effectiveRefundAmount = refund.getCalculatedRefundAmount();
        }

        // 환불 가능한 금액 체크 (환불 거절에는 적용 안함)
        if (status != RefundStatus.REJECTED.getCode()) {
            int remainingAmount = refund.getRemainingRefundAmount() != null ? refund.getRemainingRefundAmount() : refund.getPaymentAmount();
            if (effectiveRefundAmount <= 0 || effectiveRefundAmount > remainingAmount) {
                response.put("success", false);
                response.put("message", "유효한 환불 금액을 입력하세요 (최대: " + remainingAmount + "원)");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // 계좌 환불 시 계좌 정보 체크
        if (refund.getRefundMethod() == 2 && status == RefundStatus.COMPLETED.getCode()) {
            if (refund.getAccountHolder() == null || refund.getBankName() == null || refund.getAccountNumber() == null) {
                response.put("success", false);
                response.put("message", "사용자의 계좌 정보가 등록되어 있지 않습니다. 계좌 등록 후 다시 시도해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        try {
            RefundStatus refundStatus = RefundStatus.fromCode(status);
            boolean refundSuccess = true;

            // 카드(1) 또는 간편 결제(3)일 경우 포트원 호출
            if ((refund.getRefundMethod() == 1 || refund.getRefundMethod() == 3) && refundStatus == RefundStatus.COMPLETED) {
                String accessToken = getAccessToken();
                refundSuccess = requestRefund(refund, effectiveRefundAmount, accessToken);
            } else if (refund.getRefundMethod() == 2 && refundStatus == RefundStatus.COMPLETED) {
                log.info("계좌 환불 처리: 환불 번호 {}, 금액 {}, 계좌 정보: {} {}",
                        refundId, effectiveRefundAmount, refund.getAccountHolder(), refund.getAccountNumber());
            }

            if (!refundSuccess) {
                response.put("success", false);
                response.put("message", "환불 요청 실패");
                return ResponseEntity.status(500).body(response);
            }

            // 선택된 항목의 상태 업데이트
            if (!selectedItems.isEmpty()) {
                for (RefundDTO.OrderDetailDTO item : refund.getRequestedItems()) {
                    if (selectedItems.containsKey(item.getOrderDetailId())) {
                        refundService.updateOrderDetailRefundStatus(item.getOrderDetailId(), status);
                    }
                }
            }

            // 전체 환불 상태 및 금액 업데이트
            int newRefundAmount = (refund.getRefundAmount() != null ? refund.getRefundAmount() : 0) + effectiveRefundAmount;
            refundService.updateRefundStatus(refundId, refundStatus, effectiveRefundAmount);
            refundService.updateRefundAmount(refundId, newRefundAmount);

            int newRemainingAmount = (refund.getRemainingRefundAmount() != null ? refund.getRemainingRefundAmount() : refund.getPaymentAmount()) - effectiveRefundAmount;
            if (newRemainingAmount < 0) {
                newRemainingAmount = 0;
            }
            refundService.updateRemainingRefundAmount(refundId, newRemainingAmount);

            response.put("success", true);
            response.put("message", refundStatus == RefundStatus.REJECTED ? "환불이 거절되었습니다" : "환불이 성공적으로 처리됨");
            response.put("refund", refundService.getRefundDetail(refundId));
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("환불 처리 중 오류 발생 refundId: {}, 오류 내용: {}", refundId, e.getMessage());
            response.put("success", false);
            response.put("message", "환불 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private String getAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("imp_key", impKey);
        json.put("imp_secret", impSecret);

        log.info("포트원 액세스 토큰 요청 imp_key: {}, imp_secret: {}", impKey, impSecret);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.iamport.kr/users/getToken")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : "Response Body 없음";
            log.info("포트원 응답 : HTTP {}, Response Body: {}", response.code(), responseBodyString);
            if (!response.isSuccessful()) {
                log.error("엑세스 토큰 요청 실패 : HTTP {}, 응답 본문: {}", response.code(), responseBodyString);
                throw new IOException("엑세스 토큰 요청 실패 : HTTP " + response.code() + " - " + responseBodyString);
            }
            JSONObject responseBody = new JSONObject(responseBodyString);
            if (responseBody.has("response")) {
                JSONObject responseData = responseBody.getJSONObject("response");
                return responseData.getString("access_token");
            } else {
                throw new IOException("Response 에서 액세스 토큰을 찾을 수 없음 : " + responseBodyString);
            }
        }
    }

    private boolean requestRefund(RefundDTO refund, int amount, String accessToken) throws IOException {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("imp_uid", refund.getTransactionId());
        json.put("amount", amount);
        json.put("reason", refund.getRefundReason());
        json.put("checksum", refund.getRemainingRefundAmount() != null ? refund.getRemainingRefundAmount() : refund.getPaymentAmount());

        log.info("환불 Request Body: {}", json.toString());

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.iamport.kr/payments/cancel")
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : "Response Body 없음";
            log.info("포트원 응답2 : HTTP {}, Response Body: {}", response.code(), responseBodyString);
            if (!response.isSuccessful()) {
                log.error("환불 요청 실패 : HTTP {}, Response Body: {}", response.code(), responseBodyString);
                if (response.code() == 404) {
                    log.error("유효하지 않은 transactionId: {}", refund.getTransactionId());
                }
                return false;
            }
            JSONObject responseBody = new JSONObject(responseBodyString);
            return responseBody.optInt("code", -1) == 0;
        }
    }
}
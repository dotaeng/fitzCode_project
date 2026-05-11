package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.service.AdminOrderService;
import kr.co.fitzcode.common.dto.AdminOrderDTO;
import kr.co.fitzcode.common.dto.AdminOrderDetailDTO;
import kr.co.fitzcode.common.dto.DeliveryDTO;
import kr.co.fitzcode.common.enums.DeliveryStatus;
import kr.co.fitzcode.common.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService orderService;

    // 주문 목록 조회
    @GetMapping
    public String getOrderList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            Model model) {
        List<AdminOrderDTO> orders = orderService.getOrderList(page, size, status, sortBy);
        int totalCount = orderService.getTotalOrderCount(status);
        int totalPages = orderService.calculateTotalPages(totalCount, size);
        int[] pageRange = orderService.getPageRange(page, totalPages);

        model.addAttribute("orders", orders != null ? orders : List.of());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("pageRange", pageRange);

        return "admin/order/orderList";
    }

    // 특정 주문 상세 정보 조회
    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        AdminOrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);

        if (orderDetail == null) {
            log.warn("주문 상세 정보 없음 주문 ID: {}", orderId);
        } else {
            log.info("주문 ID: {}", orderDetail.getOrderId());
            log.info("총 금액: {}원", orderDetail.getTotalAmount());
            log.info("이름: {}", orderDetail.getUsername());
            log.info("상품 개수: {}개", (orderDetail.getItems() != null ? orderDetail.getItems().size() : 0));
        }

        model.addAttribute("orderDetail", orderDetail != null ? orderDetail : new AdminOrderDetailDTO());
        return "admin/order/orderDetail";
    }

    // 배송 상태 업데이트
    @PostMapping("/{orderId}/updateDelivery")
    public String updateDeliveryStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("trackingNumber") String trackingNumber,
            @RequestParam("deliveryStatus") Integer status,
            Model model) {
        log.info("Updating delivery status for orderId: {}, status: {}, trackingNumber: {}", orderId, status, trackingNumber);

        // "배송중" 또는 "배송완료" 상태일 때만 운송장 번호 필수
        if ((status == DeliveryStatus.IN_TRANSIT.getCode() || status == DeliveryStatus.DELIVERED.getCode()) &&
                (trackingNumber == null || trackingNumber.trim().isEmpty())) {
            model.addAttribute("errorMessage", "운송장 번호를 입력해주세요.");
            AdminOrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);
            model.addAttribute("orderDetail", orderDetail != null ? orderDetail : new AdminOrderDetailDTO());
            return "admin/order/orderDetail";
        }

        DeliveryDTO delivery = new DeliveryDTO();
        delivery.setOrderId(orderId);
        delivery.setTrackingNumber(trackingNumber);
        delivery.setDeliveryStatus(DeliveryStatus.fromCode(status));

        // 배송 상태가 "배송 준비중" 또는 "배송중"으로 변경 시 deliveredAt 초기화
        if (status == DeliveryStatus.PENDING.getCode() || status == DeliveryStatus.IN_TRANSIT.getCode()) {
            delivery.setDeliveredAt(null);
            log.info("Reset deliveredAt to null for orderId: {}", orderId);
        } else if (status == DeliveryStatus.IN_TRANSIT.getCode() && delivery.getShippedAt() == null) {
            delivery.setShippedAt(LocalDateTime.now());
            log.info("Set shippedAt to now for orderId: {}", orderId);
        } else if (status == DeliveryStatus.DELIVERED.getCode() && delivery.getDeliveredAt() == null) {
            delivery.setDeliveredAt(LocalDateTime.now());
            log.info("Set deliveredAt to now for orderId: {}", orderId);
        }

        orderService.updateDelivery(delivery);
        orderService.updateOrderStatus(orderId, status);

        return "redirect:/admin/orders/" + orderId;
    }
}
package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.service.AdminRefundService;
import kr.co.fitzcode.common.dto.RefundDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/products/refund")
@RequiredArgsConstructor
public class AdminRefundController {

    private final AdminRefundService refundService;

    @GetMapping
    public String getRefundList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "orderId", required = false) String orderId,
            Model model) {

        List<RefundDTO> refunds = refundService.getRefundList(page, size, status, orderId);
        int totalCount = refundService.getRefundCount(status, orderId);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("refunds", refunds);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("status", status);
        model.addAttribute("orderId", orderId);

        return "admin/refund/refundList";
    }

    @GetMapping("/{refundId}")
    public String getRefundDetail(@PathVariable Long refundId, Model model) {
        log.info("환불 상세 조회 : 환불 ID: {}", refundId);

        RefundDTO refund = refundService.getRefundDetail(refundId);
        model.addAttribute("refund", refund);

        return "admin/refund/refundDetail";
    }
}
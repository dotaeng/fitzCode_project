package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.service.OrderService;
import kr.co.fitzcode.common.dto.OrderDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderService orderService;

    @GetMapping("/admin/products/orderDetail/{productId}")
    public String getOrderDetail(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        int totalRecords = orderService.countOrderDetailsByProductId(productId);
        int totalPages = (int) Math.ceil((double) totalRecords / size);
        int offset = (page - 1) * size;

        List<OrderDetailDTO> orderDetails = orderService.getOrderDetailsByProductIdWithPagination(productId, offset, size);

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(startPage + 4, totalPages);

        model.addAttribute("productId", productId);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/orderDetail";
    }
}
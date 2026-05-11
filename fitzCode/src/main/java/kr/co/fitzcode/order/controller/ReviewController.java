package kr.co.fitzcode.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ReviewController {

    @GetMapping("/review/new/{orderDetailId}")
    public String newReview(@PathVariable int orderDetailId, Model model) {
        log.info("orderDetailId: {}", orderDetailId);
        model.addAttribute("orderDetailId", orderDetailId);
        return "order/review";
    }
}

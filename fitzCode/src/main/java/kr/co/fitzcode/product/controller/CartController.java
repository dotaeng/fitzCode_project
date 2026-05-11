package kr.co.fitzcode.product.controller;

import kr.co.fitzcode.common.dto.CartProductDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;
import kr.co.fitzcode.common.util.SecurityUtils;
import kr.co.fitzcode.product.service.CartService;
import kr.co.fitzcode.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model) {
        return "order/cart";
    }
}

package kr.co.fitzcode.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.fitzcode.common.dto.CartDTO;
import kr.co.fitzcode.common.dto.CartProductDTO;
import kr.co.fitzcode.common.service.UserService;
import kr.co.fitzcode.common.util.SecurityUtils;
import kr.co.fitzcode.product.service.CartService;
import kr.co.fitzcode.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartApiController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "장바구니 수량 조회", description = "현재 사용자의 장바구니 상품 수량 조회")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(Principal principal) {
        log.info("GET /api/cart/count 요청 처리");
        if (principal == null) {
            log.warn("Principal이 null입니다 - 인증되지 않은 접근");
            Map<String, Integer> response = new HashMap<>();
            response.put("count", 0); // 비인증 사용자는 0 return
            return ResponseEntity.ok(response);
        }

        int userId = securityUtils.getUserId(); // 인증된 사용자 ID 가져오기
        int cartCount = cartService.getCartCount(userId); // 장바구니 수량 계산
        Map<String, Integer> response = new HashMap<>();
        response.put("count", cartCount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니에 상품 추가", description = "제품 ID와 사이즈 코드로 장바구니에 상품 추가")
    @PostMapping("/addToCart")
    public void addToCart(@RequestParam int productId, @RequestParam int sizeCode) {
        // get userid
        int userId = securityUtils.getUserId();
        int productSizeId = productService.getProductSizeIdByProductSizeAndCode(productId, sizeCode);
        log.info("productId>>>>>>>>>>>>>>>>>>>>: {}", productId);
        log.info("sizeCode>>>>>>>>>>>>>>>>>>>>: {}", sizeCode);
        log.info("productSizeId>>>>>>>>>>>>>>>>>>>>>: {}", productSizeId);
        log.info("userId>>>>>>>>>>>>>>>>>>>>>>>>>: {}", userId);

        CartDTO cartDTO = CartDTO.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .productSizeId(productSizeId)
                .build();
        cartService.addProductToCart(cartDTO);
    }

    @Operation(summary = "장바구니 상품 목록 조회", description = "현재 사용자의 장바구니 상품 목록 조회")
    @GetMapping("/getCartItems")
    public ResponseEntity<List<CartProductDTO>> getCartItems() {
        int userId = SecurityUtils.getUserId();
        List<CartProductDTO> list = cartService.getCartInformationByUserId(userId);
        log.info("list : {}", list);
        for (CartProductDTO cartProductDTO : list) {
            cartProductDTO.setProductSizes(productService.getAllSizeOfProduct(cartProductDTO.getProductId()));
        }
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCart(@RequestParam int cartId) {
        int userId = securityUtils.getUserId();
        cartService.deleteByCartId(cartId);
        return ResponseEntity.ok("delete success");

    }
}
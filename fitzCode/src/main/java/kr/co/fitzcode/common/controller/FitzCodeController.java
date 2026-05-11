package kr.co.fitzcode.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.co.fitzcode.admin.service.ProductService;
import kr.co.fitzcode.admin.service.SearchLogService;
import kr.co.fitzcode.common.dto.CustomOAuth2User;
import kr.co.fitzcode.common.dto.PickProductDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "FitzCode Core API", description = "FitzCode 핵심 API 제공. 검색, 세션 관리, 슬라이더 데이터 포함")
public class FitzCodeController {

    private final SearchLogService searchLogService;
    private final ProductService productService;

    @Operation(summary = "메인 페이지 리다이렉션", description = "애플리케이션의 메인 페이지로 이동")
    @GetMapping("/")
    public String mainPage(Model model) {
        List<PickProductDTO> pickProducts = productService.getPickProducts();
        List<ProductDTO> discountProducts = productService.getTopDiscountedProducts(10);
        model.addAttribute("pickProducts", pickProducts);
        model.addAttribute("discountProducts", discountProducts);
        return "fitzCode";
    }

    @Operation(summary = "키워드 검색", description = "사용자 입력 키워드로 검색을 수행하고 로그 저장")
    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> search(@RequestParam("keyword") String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
                userId = ((CustomOAuth2User) authentication.getPrincipal()).getUserId();
            }

            searchLogService.saveSearchLog(userId, keyword);

            response.put("success", true);
            response.put("message", "검색이 성공적으로 처리되었습니다.");
            response.put("keyword", keyword);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("검색 처리 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "검색 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "검색 결과 페이지", description = "검색 키워드에 따른 결과 페이지 로드")
    @GetMapping("/search/result")
    public String searchResult(@RequestParam("keyword") String keyword) {
        return "searchResult";
    }

    @Operation(summary = "상품 검색", description = "키워드와 페이지로 상품 검색")
    @GetMapping("/api/product/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchProducts(
            @Parameter(description = "검색 키워드", required = true, example = "Nike")
            @RequestParam("keyword") String keyword,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("Searching products with keyword: {}, page: {}", keyword, page);
            List<ProductDTO> products = productService.searchProducts(keyword, page, 20);
            int totalLength = productService.countAllProducts(keyword);
            log.info("Found {} products for keyword: {}", products.size(), keyword);

            response.put("list", products);
            response.put("totalLength", totalLength);
            response.put("currentPage", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<>());
        }
    }

    @Operation(summary = "세션 연장", description = "관리자 세션의 유효 시간을 30분으로 연장")
    @PostMapping("/admin/extend-session")
    @ResponseBody
    public ResponseEntity<String> extendSession(HttpSession session) {
        try {
            session.setMaxInactiveInterval(1800);
            return ResponseEntity.ok("Session extended successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extend session");
        }
    }

    @Operation(summary = "세션 정보 조회", description = "현재 세션의 생성 시간과 남은 시간 조회")
    @GetMapping("/admin/session-info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        long creationTime = session.getCreationTime();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long lastAccessedTime = session.getLastAccessedTime();
        long currentTime = System.currentTimeMillis();
        int timeLeft = (int) ((maxInactiveInterval * 1000 - (currentTime - lastAccessedTime)) / 1000);

        response.put("creationTime", creationTime);
        response.put("timeLeft", Math.max(timeLeft, 0));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주목받는 상품 데이터 조회", description = "메인 페이지 슬라이더용 주목받는 상품 데이터 반환")
    @GetMapping("/api/pick-products")
    @ResponseBody
    public List<PickProductDTO> getPickProductsApi() {
        return productService.getPickProducts();
    }

    @Operation(summary = "할인 상품 데이터 조회", description = "메인 페이지 슬라이더용 할인 상품 데이터 반환")
    @GetMapping("/api/discount-products")
    @ResponseBody
    public List<ProductDTO> getDiscountProductsApi() {
        List<ProductDTO> discountProducts = productService.getTopDiscountedProducts(10);
        log.debug("Discount products: {}", discountProducts);
        for (ProductDTO product : discountProducts) {
            if (product.getDiscountedPrice() != null && product.getPrice() != null && product.getPrice() > 0) {
                product.calculateDiscountPercentage();
                log.debug("Product: {}, DiscountPercentage: {}", product.getProductName(), product.getDiscountPercentage());
            } else {
                log.debug("Product: {}, No discount calculated (discountedPrice: {}, price: {})",
                        product.getProductName(), product.getDiscountedPrice(), product.getPrice());
            }
        }
        return discountProducts;
    }
}
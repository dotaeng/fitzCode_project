package kr.co.fitzcode.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.fitzcode.admin.service.ProductService;
import kr.co.fitzcode.common.dto.PickProductDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Pick Product API", description = "관리자용 주목받는 상품 관리 API")
public class AdminPickController {

    private final ProductService productService;

    @Operation(summary = "주목받는 상품 관리 페이지", description = "주목받는 상품 관리 페이지를 렌더링")
    @GetMapping("/pick")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String pickProductsPage(Model model) {
        List<PickProductDTO> pickProducts = productService.getPickProducts();
        model.addAttribute("pickProducts", pickProducts);
        return "admin/pick/pick";
    }

    @Operation(summary = "주목받는 상품 목록 조회 (API)", description = "관리자가 설정한 주목받는 상품 목록을 JSON으로 반환")
    @GetMapping("/pick-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<PickProductDTO>> getPickProducts() {
        List<PickProductDTO> pickProducts = productService.getPickProducts();
        return ResponseEntity.ok(pickProducts);
    }

    @Operation(summary = "주목받는 상품 업데이트 (API)", description = "관리자가 주목받는 상품 목록을 수정")
    @PostMapping("/pick-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<String> updatePickProducts(@RequestBody List<PickProductDTO> pickProducts) {
        productService.updatePickProducts(pickProducts);
        return ResponseEntity.ok("Pick products 업데이트 성공");
    }

    @Operation(summary = "상품 검색", description = "키워드에 따라 상품을 검색 (관리자용)")
    @GetMapping("/products/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String query,
            @Parameter(description = "페이지 번호", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 상품 수", required = false) @RequestParam(defaultValue = "10") int pageSize) {
        List<ProductDTO> products = productService.searchProducts(query, page, pageSize);
        return ResponseEntity.ok(products);
    }
}
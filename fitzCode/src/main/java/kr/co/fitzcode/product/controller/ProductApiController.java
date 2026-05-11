package kr.co.fitzcode.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.fitzcode.common.dto.CategoryDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.dto.ProductResponseDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;
import kr.co.fitzcode.product.service.CategoryService;
import kr.co.fitzcode.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "제품 관련 API 제공, 제품 목록 조회, 필터링, 사이즈 정보")
public class ProductApiController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Operation(summary = "제품 목록 조회", description = "페이지 번호로 제품 목록 조회")
    @GetMapping(value = "/list/{pageNum}")
    public ResponseEntity<List<ProductDTO>> list(
            @Parameter(description = "페이지 번호") @PathVariable Integer pageNum) {
        List<ProductDTO> list = productService.getProductsByPage(pageNum);
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "카테고리 목록 조회", description = "모든 카테고리 목록 조회")
    @GetMapping("/category")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> list = categoryService.getAllCategories();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "필터링된 제품 조회", description = "카테고리 코드와 검색어로 제품 필터링 및 페이지 조회")
    @PostMapping("/getProductsByFilter")
    public ResponseEntity<ProductResponseDTO> getProductsByFilter(@Parameter @RequestParam MultiValueMap<String, String> filters, Model model) {
        List<String> codes = filters.keySet().stream()
                .filter(key -> !key.equals("page") && !key.equals("searchText"))
                .collect(Collectors.toList());

        log.info("codes type: {}", codes.getClass().getName());

        String searchText = filters.getFirst("searchText");
        String currentPageString = filters.getFirst("page");
        int currentPage = Integer.parseInt(currentPageString);
        log.info("searchText: {}, currentPageString: {}, int page: {}", searchText, currentPageString, currentPage);
        log.info("codes: {}", codes);

        List<ProductDTO> filteredProducts;
        int totalLength;
        if ((codes == null || codes.isEmpty()) && (searchText == null || searchText.isEmpty())) {
            filteredProducts = productService.getProductsByPage(currentPage);
            totalLength = productService.getCountOfAllProducts();
        } else {
            filteredProducts = productService.getProductsByFilterAndPage(codes, searchText, currentPage);
            totalLength = productService.getProductsCountByFilter(codes, searchText);
        }
        ProductResponseDTO productResponseDTO = ProductResponseDTO.builder()
                .list(filteredProducts)
                .totalLength(totalLength)
                .currentPage(currentPage)
                .build();

        return ResponseEntity.ok().body(productResponseDTO);
    }

    @Operation(summary = "제품 사이즈 조회", description = "제품 ID로 해당 제품의 모든 사이즈 조회")
    @PostMapping("/getProductSizes")
    public ResponseEntity<List<ProductSizeDTO>> getProductSizes(
            @Parameter(description = "제품 ID") @RequestParam int productId) {
        List<ProductSizeDTO> listSize = productService.getAllSizeOfProduct(productId);
        return ResponseEntity.ok().body(listSize);
    }
}
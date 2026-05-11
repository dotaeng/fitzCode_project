package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.common.dto.PickProductDTO;
import kr.co.fitzcode.common.dto.ProductCategoryDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {

    // 상품 추가 (이미지 파일 포함)
    void addProduct(ProductDTO productDTO, MultipartFile mainImageFile, List<MultipartFile> additionalImageFiles);

    // 전체 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getAllProducts(int page, int pageSize, String sort, String keyword);

    // 특정 카테고리 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getProductsByCategory(Long categoryId, int page, int pageSize, String sort, String keyword);

    // 특정 상위 카테고리의 모든 하위 카테고리 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getProductsByParentCategory(Long parentCategoryId, int page, int pageSize, String sort, String keyword);

    // 전체 상품 개수 조회 (검색 적용)
    int countAllProducts(String keyword);

    // 특정 카테고리 상품 개수 조회 (검색 적용)
    int countProductsByCategory(Long categoryId, String keyword);

    // 특정 상위 카테고리 상품 개수 조회 (검색 적용)
    int countProductsByParentCategory(Long parentCategoryId, String keyword);

    // 상위 카테고리 목록 조회
    List<ProductCategoryDTO> getParentCategories();

    // 특정 상위 카테고리의 하위 카테고리 목록 조회
    List<ProductCategoryDTO> getChildCategories(Long parentCategoryId);

    // 상위 카테고리 -> 사이즈 조회
    List<Map<String, Object>> getSizesByParentCategory(Long parentId);

    // 엑셀 업로드
    void uploadExcel(MultipartFile excelFile, List<MultipartFile> imageFiles) throws Exception;

    // 상품 조회 검색(사용자페이지)
    List<ProductDTO> searchProducts(String keyword, int page, int pageSize);

    int countSearchProducts(String keyword);

    // 주목받는 상품 조회
    List<PickProductDTO> getPickProducts();

    // 주목받는 상품 업데이트
    void updatePickProducts(List<PickProductDTO> pickProducts);

    // 할인율 높은 상품 조회
    List<ProductDTO> getTopDiscountedProducts(int limit);
}
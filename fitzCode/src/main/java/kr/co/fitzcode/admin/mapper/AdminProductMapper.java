package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.PickProductDTO;
import kr.co.fitzcode.common.dto.ProductCategoryDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminProductMapper {

    // 상품 등록
    void insertProduct(ProductDTO productDTO);

    // 추가 이미지 등록
    void insertProductImage(
            @Param("productId") Long productId,
            @Param("imageUrl") String imageUrl,
            @Param("imageOrder") Integer imageOrder);

    // 사이즈별 재고 등록
    void insertProductSize(
            @Param("productId") Long productId,
            @Param("sizeCode") Integer sizeCode,
            @Param("stock") Integer stock);

    // 전체 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getAllProducts(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("sort") String sort,
            @Param("keyword") String keyword);

    // 특정 카테고리 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getProductsByCategory(
            @Param("categoryId") Long categoryId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("sort") String sort,
            @Param("keyword") String keyword);

    // 특정 상위 카테고리의 모든 하위 카테고리 상품 조회 (페이지네이션, 정렬, 검색 적용)
    List<ProductDTO> getProductsByParentCategory(
            @Param("parentCategoryId") Long parentCategoryId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("sort") String sort,
            @Param("keyword") String keyword);

    // 전체 상품 개수 조회 (검색 적용)
    int countAllProducts(@Param("keyword") String keyword);

    // 특정 카테고리 상품 개수 조회 (검색 적용)
    int countProductsByCategory(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword);

    // 특정 상위 카테고리의 모든 하위 카테고리 상품 개수 조회 (검색 적용)
    int countProductsByParentCategory(
            @Param("parentCategoryId") Long parentCategoryId,
            @Param("keyword") String keyword);

    // 상위 카테고리 목록 조회
    List<ProductCategoryDTO> getParentCategories();

    // 특정 상위 카테고리의 하위 카테고리 목록 조회
    List<ProductCategoryDTO> getChildCategories(@Param("parentId") Long parentId);

    // 카테고리 검증
    int countCategoryById(Long categoryId);

    // 상품 검색 필터 적용 조회
    List<ProductDTO> getProductsByFilter(Map<String, Object> params);

    // 상품 검색 필터 적용 개수 조회
    int getProductsCountByFilter(Map<String, Object> params);

    List<PickProductDTO> selectPickProducts();
    void deleteAllPickProducts();
    void insertPickProduct(PickProductDTO dto);
    List<ProductDTO> selectTopDiscountedProducts(@Param("limit") int limit);
}
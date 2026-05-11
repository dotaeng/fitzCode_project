package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDetailMapper {

    // 상품 기본 정보 조회
    ProductDetailDTO findProductDetailById(@Param("productId") Long productId);

    // 상품 이미지 목록 조회
    List<ProductImageDTO> findProductImagesById(@Param("productId") Long productId);

    // 상품 사이즈 목록 조회
    List<ProductSizeDTO> findSizesByProductId(@Param("productId") Long productId);

    // 사이즈 재고 수정
    int updateProductSizeStock(ProductSizeDTO productSizeDTO);

    // 할인 가격 수정
    int updateDiscountedPrice(@Param("productId") Long productId, @Param("discountedPrice") Integer discountedPrice);

    // 새로운 사이즈 데이터 인서트
    int insertProductSize(ProductSizeDTO productSizeDTO);

    // 상품 상태 업데이트
    void updateProductStatus(@Param("productId") Long productId, @Param("status") Integer status);

    // 상품 삭제
    void deleteProduct(@Param("productId") Long productId);

    // 리뷰 목록 조회 (페이징 추가)
    List<ReviewDTO> findReviewsByProductId(Map<String, Object> params);

    // 리뷰 개수 조회
    int countReviewsByProductId(@Param("productId") Long productId);

    // 리뷰 단일 조회 (삭제 확인용)
    ReviewDTO findReviewById(@Param("reviewId") Long reviewId);

    // 리뷰 이미지 조회
    List<String> findReviewImagesByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 삭제
    void deleteReview(@Param("reviewId") Long reviewId);

    // Q&A 목록 조회 (페이징 및 필터링 추가)
    List<QnaDTO> findQnasByProductId(Map<String, Object> params);

    // Q&A 목록 조회 (상태별, 페이징 추가)
    List<QnaDTO> findQnasByProductIdAndStatus(Map<String, Object> params);

    // Q&A 개수 조회
    int countQnasByProductId(@Param("productId") Long productId);

    // Q&A 개수 조회 (상태별)
    int countQnasByProductIdAndStatus(@Param("productId") Long productId, @Param("status") int status);

    // Q&A 단일 조회 (삭제 확인용)
    QnaDTO findQnaById(@Param("qnaId") Long qnaId);

    // Q&A 답변 추가/수정
    void updateQnaAnswer(@Param("qnaId") Long qnaId, @Param("answer") String answer);

    // Q&A 삭제
    void deleteQna(@Param("qnaId") Long qnaId);

    // 대표 이미지 URL 업데이트
    void updateProductMainImage(@Param("productId") Long productId, @Param("imageUrl") String imageUrl);

    // 특정 상품의 추가 이미지 삭제
    void deleteProductImages(@Param("productId") Long productId);

    // 새로운 추가 이미지 삽입
    void insertProductImage(ProductImageDTO productImageDTO);

    // 특정 이미지 ID에 해당하는 이미지를 삭제
    int deleteProductImagesById(@Param("productImageId") Long productImageId);

    // 이미지 삭제 후 image_order 재정의
    void updateProductImageOrder(Long productImageId, Integer imageOrder);

    // 상품 재고 삭제
    void deleteProductSizes(@Param("productId") Long productId);
}
package kr.co.fitzcode.admin.service;


import kr.co.fitzcode.common.dto.ProductDetailDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;
import kr.co.fitzcode.common.dto.QnaDTO;
import kr.co.fitzcode.common.dto.ReviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductDetailService {

    // 상품 상세 정보를 조회
    ProductDetailDTO getProductDetail(Long productId);

    // 상품 할인 가격을 업데이트
    void updateDiscountedPrice(Long productId, Integer discountedPrice);

    // 상품의 사이즈 목록 조회
    List<ProductSizeDTO> getSizesByProductId(Long productId);

    // 상품 사이즈 목록을 업데이트
    void updateSizes(Long productId, List<ProductSizeDTO> sizes);

    // 카테고리별 모든 사이즈 목록을 가져옴
    List<ProductSizeDTO> getAllSizes(boolean isShoeCategory, boolean isClothingCategory, List<ProductSizeDTO> existingSizes);

    // 상품 상태를 업데이트
    void updateStatus(Long productId, Integer status);

    // 상품 삭제
    void deleteProduct(Long productId);

    // 특정 상품의 리뷰 목록 조회 (페이징 추가)
    List<ReviewDTO> getReviewsByProductId(Long productId, int offset, int pageSize);

    // 리뷰 개수 조회
    int getReviewCountByProductId(Long productId);

    // 리뷰 삭제
    void deleteReview(Long reviewId);

    // 특정 상품의 Q&A 목록 조회 (페이징 및 필터링 추가)
    List<QnaDTO> getQnasByProductId(Long productId, String filter, int offset, int pageSize);

    // Q&A 개수 조회 (필터링 추가)
    int getQnaCountByProductId(Long productId, String filter);

    // Q&A 답변 추가
    void addQnaAnswer(Long qnaId, String answer);

    // Q&A 답변 수정
    void updateQnaAnswer(Long qnaId, String answer);

    // Q&A 삭제
    void deleteQna(Long qnaId);

    // 이미지 수정 (S3 업로드 및 DB 업데이트)
    void updateProductImages(Long productId, MultipartFile mainImage, List<MultipartFile> additionalImages, List<Long> deleteImageIds);
}
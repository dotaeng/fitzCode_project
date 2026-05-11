package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.ProductDetailMapper;
import kr.co.fitzcode.common.dto.*;
import kr.co.fitzcode.common.enums.ProductSize;
import kr.co.fitzcode.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {

    private static final Logger log = LoggerFactory.getLogger(ProductDetailServiceImpl.class);
    private final ProductDetailMapper productDetailMapper;
    private final S3Service s3Service;

    // 상품 ID를 기반으로 상품의 상세 정보를 조회하고 이미지 및 사이즈 정보를 포함하여 반환
    @Override
    public ProductDetailDTO getProductDetail(Long productId) {
        ProductDetailDTO product = productDetailMapper.findProductDetailById(productId);
        if (product == null) {
            return null;
        }

        List<ProductImageDTO> images = productDetailMapper.findProductImagesById(productId);
        log.info("이미지 조회됨: {}", images != null ? images.size() : "없음");
        if (images != null) {
            for (ProductImageDTO img : images) {
                if (img.getImageUrl() == null || img.getImageUrl().isEmpty()) {
                    img.setImageUrl("/img/fallback.jpeg");
                }
            }
        }
        product.setImages(images != null ? images : new ArrayList<>());

        List<ProductSizeDTO> sizes = productDetailMapper.findSizesByProductId(productId);
        if (sizes != null) {
            for (ProductSizeDTO size : sizes) {
                size.getSizeDescription();
            }
        }
        product.setSizes(sizes != null ? sizes : new ArrayList<>());

        Integer categoryId = product.getCategoryId();
        boolean isShoeCategory = isShoeCategory(categoryId);
        boolean isClothingCategory = isClothingCategory(categoryId);

        List<ProductSizeDTO> allSizes = getAllSizes(isShoeCategory, isClothingCategory, product.getSizes());
        product.setAllSizes(allSizes);
        product.setSizes(new ArrayList<>(allSizes));

        return product;
    }

    // 카테고리 ID를 기반으로 해당 카테고리가 신발 카테고리인지 판단
    private boolean isShoeCategory(Integer categoryId) {
        if (categoryId == null) return false;
        return categoryId == 1 || categoryId == 4 || categoryId == 5 || categoryId == 6 ||
                categoryId == 10 || categoryId == 11 || categoryId == 12;
    }

    // 카테고리 ID를 기반으로 해당 카테고리가 의류 카테고리인지 판단
    private boolean isClothingCategory(Integer categoryId) {
        if (categoryId == null) return false;
        return (categoryId == 2 || categoryId == 7 || categoryId == 8 || categoryId == 9) ||
                (categoryId == 3 || categoryId == 14 || categoryId == 15 || categoryId == 16);
    }

    // 신발 또는 의류 카테고리에 따라 가능한 모든 사이즈 목록을 생성하고 기존 사이즈 정보를 반영
    @Override
    public List<ProductSizeDTO> getAllSizes(boolean isShoeCategory, boolean isClothingCategory, List<ProductSizeDTO> existingSizes) {
        List<ProductSizeDTO> allSizes = new ArrayList<>();
        for (ProductSize size : ProductSize.values()) {
            if (isShoeCategory && size.getCode() <= 9) {
                ProductSizeDTO dto = new ProductSizeDTO();
                dto.setSizeCode(size.getCode());
                if (existingSizes != null) {
                    for (ProductSizeDTO existing : existingSizes) {
                        if (existing.getSizeCode() != null && existing.getSizeCode().equals(size.getCode())) {
                            dto.setProductSizeId(existing.getProductSizeId());
                            dto.setStock(existing.getStock() != null ? existing.getStock() : 0);
                            break;
                        }
                    }
                }
                if (dto.getStock() == null) dto.setStock(0);
                allSizes.add(dto);
            } else if (isClothingCategory && size.getCode() >= 10) {
                ProductSizeDTO dto = new ProductSizeDTO();
                dto.setSizeCode(size.getCode());
                if (existingSizes != null) {
                    for (ProductSizeDTO existing : existingSizes) {
                        if (existing.getSizeCode() != null && existing.getSizeCode().equals(size.getCode())) {
                            dto.setProductSizeId(existing.getProductSizeId());
                            dto.setStock(existing.getStock() != null ? existing.getStock() : 0);
                            break;
                        }
                    }
                }
                if (dto.getStock() == null) dto.setStock(0);
                allSizes.add(dto);
            }
        }
//        log.info("신발 카테고리 : {}, 의류 카테고리 : {}, 전체 사이즈 개수: {}", isShoeCategory, isClothingCategory, allSizes.size());
        return allSizes;
    }

    // 상품 ID와 할인 가격을 받아 해당 상품의 할인 가격을 업데이트
    @Override
    public void updateDiscountedPrice(Long productId, Integer discountedPrice) {
        if (discountedPrice == null) {
            discountedPrice = 0;
        }
        productDetailMapper.updateDiscountedPrice(productId, discountedPrice);
    }

    // 상품 ID를 기반으로 해당 상품의 사이즈 목록을 조회
    @Override
    public List<ProductSizeDTO> getSizesByProductId(Long productId) {
        return productDetailMapper.findSizesByProductId(productId);
    }

    // 상품 ID와 사이즈 목록을 받아 기존 사이즈를 업데이트하거나 새 사이즈를 추가
    @Override
    public void updateSizes(Long productId, List<ProductSizeDTO> sizes) {
        if (sizes != null) {
            for (ProductSizeDTO dto : sizes) {
                dto.setProductId(productId);
//                log.info("사이즈 : 상품 사이즈 ID={}, 사이즈 코드={}, 재고={}", dto.getProductSizeId(), dto.getSizeCode(), dto.getStock());
                if (dto.getSizeCode() == null) {
//                    log.error("사이즈 코드가 null 상품 ID={}, DTO={}", productId, dto);
                    throw new IllegalArgumentException("사이즈코드는 null이면 안됨");
                }
                if (dto.getProductSizeId() == null) {
                    productDetailMapper.insertProductSize(dto);
                } else {
                    productDetailMapper.updateProductSizeStock(dto);
                }
            }
        }
    }

    // 상품 ID와 상태 값을 받아 상품의 상태를 업데이트
    @Override
    public void updateStatus(Long productId, Integer status) {
//        log.info("상품 ID={}의 상태를 {}로 업데이트", productId, status);
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new IllegalArgumentException("유효하지 않은 상태 값: " + status);
        }
        productDetailMapper.updateProductStatus(productId, status);
    }

    // 상품 ID를 가져와서 상품 삭제
    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        // 삭제할 상품 정보
        ProductDetailDTO product = productDetailMapper.findProductDetailById(productId);
        if (product == null) {
            throw new IllegalArgumentException("삭제하려는 상품이 존재하지 않습니다. ID: " + productId);
        }

        // S3에서 이미지 삭제
        // 대표 이미지 삭제
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty() && !product.getImageUrl().equals("/img/fallback.jpeg")) {
            try {
                String mainImageKey = extractS3Key(product.getImageUrl());
                s3Service.deleteFile(mainImageKey);
                log.info("S3에서 대표 이미지 삭제 성공: URL={}", product.getImageUrl());
            } catch (Exception e) {
                log.error("S3 대표 이미지 삭제 실패: URL={}, 에러: {}", product.getImageUrl(), e.getMessage());
                throw new RuntimeException("S3 대표 이미지 삭제 실패", e);
            }
        }

        // 추가 이미지 삭제
        List<ProductImageDTO> images = productDetailMapper.findProductImagesById(productId);
        if (images != null && !images.isEmpty()) {
            for (ProductImageDTO img : images) {
                if (img.getImageUrl() != null && !img.getImageUrl().isEmpty() && !img.getImageUrl().equals("/img/fallback.jpeg")) {
                    try {
                        String imageKey = extractS3Key(img.getImageUrl());
                        s3Service.deleteFile(imageKey);
                        log.info("S3에서 추가 이미지 삭제 성공: URL={}", img.getImageUrl());
                    } catch (Exception e) {
                        log.error("S3 추가 이미지 삭제 실패: URL={}, 에러: {}", img.getImageUrl(), e.getMessage());
                        throw new RuntimeException("S3 추가 이미지 삭제 실패", e);
                    }
                }
            }
        }

        // DB 데이터 삭제
        productDetailMapper.deleteProductImages(productId); // 추가 이미지 삭제
        productDetailMapper.deleteProductSizes(productId);  // 사이즈 재고 삭제
        productDetailMapper.deleteProduct(productId);       // 상품 삭제
        log.info("상품 ID={} 및 연관 데이터 삭제 완료", productId);
    }

    // S3 URL에서 키 추출 메서드 추가
    private String extractS3Key(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path; // 선행 슬래시 제거
        } catch (URISyntaxException e) {
            log.error("유효하지 않은 S3 URL: {}", imageUrl, e);
            return imageUrl; // 기본값으로 URL 반환 (환경에 따라 조정 필요)
        }
    }

    // 상품 ID와 페이징 정보를 받아 해당 상품의 리뷰 목록을 조회
    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId, int offset, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("offset", offset);
        params.put("limit", pageSize);
        List<ReviewDTO> reviews = productDetailMapper.findReviewsByProductId(params);
        for (ReviewDTO review : reviews) {
            List<String> imageUrls = productDetailMapper.findReviewImagesByReviewId(review.getReviewId());
            review.setImageUrls(imageUrls != null ? imageUrls : new ArrayList<>());
        }
        return reviews;
    }

    // 상품 ID를 받아 해당 상품의 리뷰 개수를 반환
    @Override
    public int getReviewCountByProductId(Long productId) {
        return productDetailMapper.countReviewsByProductId(productId);
    }

    // 리뷰 ID를 받아 해당 리뷰를 삭제
    @Override
    public void deleteReview(Long reviewId) {
        ReviewDTO review = productDetailMapper.findReviewById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("삭제하려는 리뷰가 존재하지 않습니다. ID: " + reviewId);
        }
        productDetailMapper.deleteReview(reviewId);
//        log.info("리뷰 ID={}가 삭제됨", reviewId);
    }

    // 상품 ID, 필터 조건, 페이징 정보를 받아 해당 상품의 Q&A 목록을 조회
    @Override
    public List<QnaDTO> getQnasByProductId(Long productId, String filter, int offset, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("offset", offset);
        params.put("limit", pageSize);

        List<QnaDTO> qnas;
        if ("waiting".equals(filter)) {
            params.put("status", 1);
            qnas = productDetailMapper.findQnasByProductIdAndStatus(params);
        } else if ("completed".equals(filter)) {
            params.put("status", 2);
            qnas = productDetailMapper.findQnasByProductIdAndStatus(params);
        } else {
            qnas = productDetailMapper.findQnasByProductId(params);
        }
//        log.info("getQnasByProductId: productId={}, filter={}, size={}", productId, filter, qnas.size());
        return qnas;
    }

    // 상품 ID와 필터 조건을 받아 해당 상품의 Q&A 개수를 반환
    @Override
    public int getQnaCountByProductId(Long productId, String filter) {
        int count;
        if ("waiting".equals(filter)) {
            count = productDetailMapper.countQnasByProductIdAndStatus(productId, 1);
        } else if ("completed".equals(filter)) {
            count = productDetailMapper.countQnasByProductIdAndStatus(productId, 2);
        } else {
            count = productDetailMapper.countQnasByProductId(productId);
        }
//        log.info("getQnaCountByProductId: productId={}, filter={}, count={}", productId, filter, count);
        return count;
    }

    // Q&A ID와 답변을 받아 새로운 답변을 추가
    @Override
    public void addQnaAnswer(Long qnaId, String answer) {
        if (qnaId == null || answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Q&A ID 또는 답변이 유효하지 않습니다.");
        }
        productDetailMapper.updateQnaAnswer(qnaId, answer);
//        log.info("Q&A ID={}에 답변 추가됨: {}", qnaId, answer);
    }

    // Q&A ID와 수정된 답변을 받아 기존 답변을 업데이트
    @Override
    public void updateQnaAnswer(Long qnaId, String answer) {
        if (qnaId == null || answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Q&A ID 또는 수정할 답변이 유효하지 않습니다.");
        }
        productDetailMapper.updateQnaAnswer(qnaId, answer);
//        log.info("Q&A ID={}의 답변 수정됨: {}", qnaId, answer);
    }

    // Q&A ID를 받아 해당 Q&A를 삭제
    @Override
    public void deleteQna(Long qnaId) {
        QnaDTO qna = productDetailMapper.findQnaById(qnaId);
        if (qna == null) {
            throw new IllegalArgumentException("삭제하려는 Q&A가 존재하지 않습니다. ID: " + qnaId);
        }
        productDetailMapper.deleteQna(qnaId);
//        log.info("Q&A ID={}가 삭제됨", qnaId);
    }

    // 상품 ID와 대표 이미지, 추가 이미지를 받아 S3에 업로드하고 기존 이미지를 선택적으로 업데이트
    @Transactional
    @Override
    public void updateProductImages(Long productId, MultipartFile mainImage, List<MultipartFile> additionalImages, List<Long> imageIdsToDelete) {
        ProductDetailDTO product = productDetailMapper.findProductDetailById(productId);
        if (product == null) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다. ID: " + productId);
        }
//        log.info("현재 대표 이미지: {}", product.getImageUrl());

        // 1. 대표 이미지 처리
        if (mainImage != null && !mainImage.isEmpty()) {
            String oldMainImageUrl = product.getImageUrl();
            String newMainImageUrl = s3Service.uploadFile(mainImage, "product-images/main");
            productDetailMapper.updateProductMainImage(productId, newMainImageUrl);
            if (oldMainImageUrl != null && !oldMainImageUrl.equals("/img/fallback.jpeg")) {
                try {
                    s3Service.deleteFile(oldMainImageUrl);
                } catch (Exception e) {
//                    log.error("S3 대표 이미지 삭제 실패: URL={}, 에러: {}", oldMainImageUrl, e.getMessage());
                    throw new RuntimeException("S3 대표 이미지 삭제 실패", e);
                }
            }
//            log.info("대표 이미지 수정: 상품 ID={}, 이전 URL={}, 새 URL={}", productId, oldMainImageUrl, newMainImageUrl);
        }

        // 2. 기존 추가 이미지 처리
        List<ProductImageDTO> existingImages = productDetailMapper.findProductImagesById(productId);
//        log.info("기존 이미지 목록: {}", existingImages);
        if (existingImages != null && !existingImages.isEmpty()) {
            if (imageIdsToDelete != null && !imageIdsToDelete.isEmpty()) {
//                log.info("삭제할 이미지 ID 목록: {}", imageIdsToDelete);
                for (ProductImageDTO img : new ArrayList<>(existingImages)) {
                    if (imageIdsToDelete.contains(img.getProductImageId())) {
                        // 대표 이미지가 삭제 대상인지 확인
                        if (product.getImageUrl() != null && product.getImageUrl().equals(img.getImageUrl())) {
                            productDetailMapper.updateProductMainImage(productId, null);
//                            log.info("대표 이미지 삭제됨: 상품 ID={}, 이미지 URL={}", productId, img.getImageUrl());
                        }
                        if (img.getImageUrl() != null && !img.getImageUrl().equals("/img/fallback.jpeg")) {
                            try {
                                s3Service.deleteFile(img.getImageUrl());
//                                log.info("S3 이미지 삭제 성공: URL={}", img.getImageUrl());
                            } catch (Exception e) {
//                                log.error("S3 이미지 삭제 실패: URL={}, 에러: {}", img.getImageUrl(), e.getMessage());
                                throw new RuntimeException("S3 삭제 실패", e);
                            }
                        }
//                        log.info("DB에서 이미지 삭제 시도: imageId={}", img.getProductImageId());
                        int deletedRows = productDetailMapper.deleteProductImagesById(img.getProductImageId());
                        if (deletedRows > 0) {
//                            log.info("DB에서 이미지 삭제 성공: imageId={}", img.getProductImageId());
                        } else {
//                            log.warn("DB에서 이미지 삭제 실패: imageId={}", img.getProductImageId());
                        }
                        existingImages.remove(img);
//                        log.info("기존 추가 이미지 삭제: 상품 ID={}, 이미지 ID={}", productId, img.getProductImageId());
                    }
                }
                // 삭제 후 image_order 재정의
                int order = 1;
                for (ProductImageDTO img : existingImages) {
                    img.setImageOrder(order++);
                    productDetailMapper.updateProductImageOrder(img.getProductImageId(), img.getImageOrder());
//                    log.info("image_order 재정의: imageId={}, newOrder={}", img.getProductImageId(), img.getImageOrder());
                }
            }
        }

        // 3. 새로운 추가 이미지 처리 + 빈 파일인 경우 필터링
        if (additionalImages != null && !additionalImages.isEmpty()) {
            List<MultipartFile> validFiles = additionalImages.stream()
                    .filter(file -> file != null && !file.isEmpty() && file.getSize() > 0)
                    .collect(Collectors.toList());
            if (!validFiles.isEmpty()) {
                List<String> newImageUrls = s3Service.uploadFiles(validFiles, "product-images/additional");
                for (int i = 0; i < newImageUrls.size(); i++) {
                    ProductImageDTO newImage = new ProductImageDTO();
                    newImage.setProductId(productId);
                    newImage.setImageUrl(newImageUrls.get(i));
                    newImage.setImageOrder(existingImages.size() + i + 1);
                    productDetailMapper.insertProductImage(newImage);
//                    log.info("추가 이미지 업로드: 상품 ID={}, 이미지 URL={}, 순서={}", productId, newImageUrls.get(i), newImage.getImageOrder());
                }
//                log.info("새로운 추가 이미지: 상품 ID={}, 업로드된 이미지 수={}", productId, newImageUrls.size());
            } else {
//                log.info("추가 이미지가 없음, 업로드 생략함: productId={}", productId);
            }
        }
    }
}
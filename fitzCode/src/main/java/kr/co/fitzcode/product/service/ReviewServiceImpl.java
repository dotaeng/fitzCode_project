package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.ReviewDTO;
import kr.co.fitzcode.common.enums.InquiryStatus;
import kr.co.fitzcode.product.mapper.ReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public ReviewServiceImpl(ReviewMapper reviewMapper, S3Client s3Client) {
        this.reviewMapper = reviewMapper;
        this.s3Client = s3Client;
    }

    @Override
    public List<ReviewDTO> getReviews(Long productId, String filter) {
        List<ReviewDTO> reviews;
        switch (filter) {
            case "photo":
                reviews = reviewMapper.findByProductIdWithImage(productId);
                break;
            case "high-rating":
                reviews = reviewMapper.findByProductIdOrderByRatingDesc(productId);
                break;
            case "low-rating":
                reviews = reviewMapper.findByProductIdOrderByRatingAsc(productId);
                break;
            case "all":
            default:
                reviews = reviewMapper.findByProductId(productId);
                break;
        }

        // 날짜 포맷팅
        SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd");
        reviews.forEach(review -> {
            if (review.getCreatedAt() != null) {
                review.setCreatedAtStr(formatter.format(review.getCreatedAt()));
            }
        });

        return reviews;
    }

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO, MultipartFile image) {
        // 리뷰 저장
        reviewMapper.insertReview(reviewDTO);

        // 이미지 업로드 및 저장
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = "reviews/" + UUID.randomUUID() + "_" + image.getOriginalFilename();
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

                String imageUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toString();
                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(imageUrl);
                reviewDTO.setImageUrls(imageUrls);

                // 리뷰 이미지 저장
                reviewMapper.insertReviewImage(reviewDTO.getReviewId(), imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image to S3", e);
            }
        }

        // 저장된 리뷰 조회
        ReviewDTO savedReview = reviewMapper.findById(reviewDTO.getReviewId());
        if (savedReview.getCreatedAt() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd");
            savedReview.setCreatedAtStr(formatter.format(savedReview.getCreatedAt()));
        }

        return savedReview;
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        int count = reviewMapper.countReviewsByUserAndProduct(userId, productId);
        return count > 0;
    }

    @Override
    public List<ReviewDTO> getUserReviews(Long userId, Long productId) {
        List<ReviewDTO> reviews = reviewMapper.findByUserIdAndProductId(userId, productId);
        // 날짜 포맷팅
        SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd");
        reviews.forEach(review -> {
            if (review.getCreatedAt() != null) {
                review.setCreatedAtStr(formatter.format(review.getCreatedAt()));
            }
        });
        return reviews;
    }

    @Override
    public ReviewDTO getReviewById(Long reviewId) {
        return reviewMapper.findById(reviewId);
    }

    @Override
    public void deleteReview(Long reviewId) {
        // 리뷰와 관련된 이미지 URL 조회
        ReviewDTO review = reviewMapper.findById(reviewId);
        if (review != null && review.getImageUrls() != null && !review.getImageUrls().isEmpty()) {
            for (String imageUrl : review.getImageUrls()) {
                try {
                    // S3에서 이미지 삭제
                    String key = extractS3KeyFromUrl(imageUrl);
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();
                    s3Client.deleteObject(deleteObjectRequest);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete image from S3: " + imageUrl, e);
                }
            }
        }

        // 리뷰 이미지 삭제
        reviewMapper.deleteReviewImages(reviewId);
        // 리뷰 삭제
        reviewMapper.deleteReview(reviewId);
    }

    // S3 URL에서 키 추출
    private String extractS3KeyFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        StringBuilder key = new StringBuilder();
        for (int i = 3; i < parts.length; i++) { // 버킷 이름과 지역 정보 다음꺼
            if (i > 3) {
                key.append("/");
            }
            key.append(parts[i]);
        }
        return key.toString();
    }


    @Override
    public Map<Integer, Integer> getRatingCounts(int productId) {

        Map<Integer, Integer> counts = new HashMap<>();
        List<Map<String, Object>> getRatingCounts = reviewMapper.getRatingCounts(productId);
        log.info("getRatingCounts: {}", getRatingCounts);

        for (Map<String, Object> result : getRatingCounts) {
            Integer rating = (Integer) result.get("rating");
            Integer count = ((Number) result.get("rating_count")).intValue();
            counts.put(rating, count);
        }
        log.info("Counts: {}", counts);
        return counts;
    }
}
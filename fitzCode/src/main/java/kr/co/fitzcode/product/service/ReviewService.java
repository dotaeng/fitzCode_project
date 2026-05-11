package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.ReviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    List<ReviewDTO> getReviews(Long productId, String filter);
    ReviewDTO createReview(ReviewDTO reviewDTO, MultipartFile image);
    boolean hasUserReviewedProduct(Long userId, Long productId);
    ReviewDTO getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
    List<ReviewDTO> getUserReviews(Long userId, Long productId);
    Map<Integer, Integer> getRatingCounts(int productId);
}
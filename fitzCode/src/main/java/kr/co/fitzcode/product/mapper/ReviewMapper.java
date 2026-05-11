package kr.co.fitzcode.product.mapper;

import kr.co.fitzcode.common.dto.ReviewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper {
    List<ReviewDTO> findByProductId(@Param("productId") Long productId);
    List<ReviewDTO> findByProductIdWithImage(@Param("productId") Long productId);
    List<ReviewDTO> findByProductIdOrderByRatingDesc(@Param("productId") Long productId);
    List<ReviewDTO> findByProductIdOrderByRatingAsc(@Param("productId") Long productId);
    ReviewDTO findById(@Param("id") Long id);
    void insertReview(ReviewDTO reviewDTO);
    void insertReviewImage(@Param("reviewId") Long reviewId, @Param("imageUrl") String imageUrl);
    int countReviewsByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
    List<ReviewDTO> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    void deleteReview(@Param("reviewId") Long reviewId);
    void deleteReviewImages(@Param("reviewId") Long reviewId);
    List<Map<String, Object>> getRatingCounts(@Param("productId") int productId);

}
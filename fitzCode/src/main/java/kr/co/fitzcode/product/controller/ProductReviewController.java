package kr.co.fitzcode.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.co.fitzcode.common.dto.ReviewDTO;
import kr.co.fitzcode.common.dto.AuthenticatedUser;
import kr.co.fitzcode.order.service.OrderService;
import kr.co.fitzcode.product.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

    private final ReviewService reviewService;
    private final OrderService orderService;

    public ProductReviewController(ReviewService reviewService, OrderService orderService) {
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    @Operation(summary = "상품후기 조회", description = "특정 상품의 상품후기 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품후기 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getReviews(
            @Parameter(description = "상품 ID", required = true) @RequestParam("productId") Long productId,
            @Parameter(description = "필터링 옵션 (all, photo, high-rating, low-rating)", required = false) @RequestParam(value = "filter", defaultValue = "all") String filter) {
        List<ReviewDTO> reviews = reviewService.getReviews(productId, filter);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "상품후기 작성", description = "새로운 상품후기를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품후기 작성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "구매 내역이 없는 사용자 또는 중복 상품후기", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createReview(
            @Parameter(description = "상품 ID", required = true) @RequestParam("productId") Long productId,
            @Parameter(description = "평점 (1~5)", required = true) @RequestParam("rating") Integer rating,
            @Parameter(description = "상품후기 내용", required = true) @RequestParam("content") String content,
            @Parameter(description = "상품후기 이미지 파일", required = false) @RequestParam(value = "image", required = false) MultipartFile image) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(null); // 인증되지 않은 사용자
        }

        // AuthenticatedUser 인터페이스로 처리
        AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = (long) userDetails.getUserId();

        // 사용자가 해당 제품을 구매했는지 확인
        if (!orderService.hasPurchasedProduct(userId.intValue(), productId)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "not_purchased");
            errorResponse.put("message", "상품을 구매한 사람만 리뷰를 작성할 수 있습니다.");
            return ResponseEntity.status(403).body(errorResponse);
        }

        // 사용자가 이미 해당 제품에 대해 상품후기를 작성했는지 확인
        if (reviewService.hasUserReviewedProduct(userId, productId)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "already_reviewed");
            errorResponse.put("message", "이미 등록된 리뷰가 있습니다.");
            return ResponseEntity.status(403).body(errorResponse);
        }

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(productId);
        reviewDTO.setRating(rating);
        reviewDTO.setContent(content);
        reviewDTO.setUserId(userId);

        ReviewDTO createdReview = reviewService.createReview(reviewDTO, image);
        return ResponseEntity.ok(createdReview);
    }

    @Operation(summary = "사용자가 작성한 상품후기 조회", description = "현재 사용자가 특정 상품에 대해 작성한 상품후기 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 상품후기 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewDTO>> getMyReviews(
            @Parameter(description = "상품 ID", required = true) @RequestParam("productId") Long productId) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(null); // 인증되지 않은 사용자
        }

        // AuthenticatedUser 인터페이스로 처리
        AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = (long) userDetails.getUserId();

        List<ReviewDTO> reviews = reviewService.getUserReviews(userId, productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "상품후기 삭제", description = "사용자가 작성한 상품후기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품후기 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable("reviewId") Long reviewId) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(null); // 인증되지 않은 사용자
        }

        // AuthenticatedUser 인터페이스로 처리
        AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = (long) userDetails.getUserId();

        // 리뷰 조회
        ReviewDTO review = reviewService.getReviewById(reviewId);
        if (review == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "not_found");
            errorResponse.put("message", "리뷰를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(errorResponse);
        }

        // 리뷰 작성자와 현재 사용자가 일치하는지 확인
        if (!review.getUserId().equals(userId)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "forbidden");
            errorResponse.put("message", "본인이 작성한 리뷰만 삭제할 수 있습니다.");
            return ResponseEntity.status(403).body(errorResponse);
        }

        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
package kr.co.fitzcode.product.controller;

import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.dto.ProductImageDTO;
import kr.co.fitzcode.product.service.ProductService;
import kr.co.fitzcode.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping(value = { "/list", "/list/{pageNum}" })
    public String list(@PathVariable(required = false) Integer pageNum, Model model) {
        return "product/productList";
    }

    @GetMapping("/detail/{productId}")
    public String detail(@PathVariable int productId, Model model) {
        ProductDTO product = productService.getProductById(productId);
        model.addAttribute("product", product);

        List<ProductImageDTO> productImageList = productService.getProductImagesByProductId(productId);
        model.addAttribute("productImageList", productImageList);

        // categoryId와 parentCategoryId 로그로 확인
        Long categoryId = product.getCategoryId();
        Long parentCategoryId = productService.getParentCategoryId(categoryId);
        log.info("Product ID: {}, Category ID: {}, Parent Category ID: {}", product.getProductId(), categoryId, parentCategoryId);

        Map<Integer, Integer> ratingCountMap = reviewService.getRatingCounts(productId);
        double averageRating = this.getAverageRating(ratingCountMap);
        double roundedAverageRating = Math.round(averageRating * 10.0) / 10.0;
        int totalReviews = this.getTotalNumberOfReviews(ratingCountMap);
        model.addAttribute("reviewCounts", ratingCountMap);
        model.addAttribute("averageRating", roundedAverageRating);
        log.info("Total Reviews: {}", totalReviews);
        model.addAttribute("totalReviews", totalReviews);

        model.addAttribute("parentCategoryId", parentCategoryId);

        return "product/productDetail";
    }

    @GetMapping("/filter")
    public String filter(Model model) {
        return "product/filter";
    }

    private double getAverageRating(Map<Integer, Integer> ratingCountMap) {
        double totalRatingSum = 0;
        int totalReviews = 0;

        for (Map.Entry<Integer, Integer> entry : ratingCountMap.entrySet()) {
            int rating = entry.getKey();  //(1, 2, 3, 4, 5)
            int count = entry.getValue(); // Number of times the rating appears

            totalRatingSum += rating * count;
            totalReviews += count;
        }

        if (totalReviews == 0) {
            return 0;
        }
        return totalRatingSum / totalReviews;
    }

    private int getTotalNumberOfReviews(Map<Integer, Integer> ratingCountMap) {
        int totalReviews = 0;

        for (Map.Entry<Integer, Integer> entry : ratingCountMap.entrySet()) {
            int count = entry.getValue();
            totalReviews += count;
        }

        return totalReviews;
    }
}
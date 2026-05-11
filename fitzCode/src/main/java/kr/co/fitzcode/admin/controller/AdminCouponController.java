package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.service.CouponService;
import kr.co.fitzcode.common.dto.CouponDTO;
import kr.co.fitzcode.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController {

    private final CouponService couponService;

    // 관리자 권한 확인
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(UserRole.ADMIN.getAuthority()));
        if (!hasAdminRole) {
            return false;
        }
        return true;
    }

    // 쿠폰 관리 페이지
    @GetMapping
    public String couponManagement(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        if (!isAdmin()) {
            return "redirect:/login";
        }

        int pageSize = 10;
        List<CouponDTO> coupons = couponService.getAllCoupons(page, pageSize);
        int totalCoupons = couponService.countAllCoupons();
        int totalPages = (int) Math.ceil((double) totalCoupons / pageSize);

        model.addAttribute("coupons", coupons);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "admin/coupon/couponManagement";
    }

    // 특정 쿠폰 조회 (모달용)
    @GetMapping("/get/{couponId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCoupon(@PathVariable("couponId") Integer couponId) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            CouponDTO coupon = couponService.getCouponById(couponId);
            if (coupon == null) {
                response.put("success", false);
                response.put("message", "쿠폰을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            // Timestamp를 JSON에 맞게 문자열로 변환
            Map<String, Object> couponMap = new HashMap<>();
            couponMap.put("couponId", coupon.getCouponId());
            couponMap.put("couponCode", coupon.getCouponCode());
            couponMap.put("description", coupon.getDescription());
            couponMap.put("couponType", coupon.getCouponType());
            couponMap.put("discountAmount", coupon.getDiscountAmount());
            couponMap.put("discountPercentage", coupon.getDiscountPercentage());
            couponMap.put("minimumOrderAmount", coupon.getMinimumOrderAmount());
            couponMap.put("expiryDate", coupon.getExpiryDate() != null ? coupon.getExpiryDate().toString() : null);
            couponMap.put("isActive", coupon.getIsActive());
            couponMap.put("createdAt", coupon.getCreatedAt() != null ? coupon.getCreatedAt().toString() : null);
            couponMap.put("validUntil", coupon.getValidUntil() != null ? coupon.getValidUntil().toString() : null);
            response.put("success", true);
            response.put("coupon", couponMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("쿠폰 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "쿠폰 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 쿠폰 추가
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCoupon(@RequestBody CouponDTO couponDTO) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            couponDTO.setIsActive(true);
            couponDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            couponService.addCoupon(couponDTO);
            response.put("success", true);
            response.put("message", "쿠폰이 성공적으로 추가되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("쿠폰 추가 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "쿠폰 추가 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 쿠폰 수정
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCoupon(@RequestBody CouponDTO couponDTO) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            couponService.updateCoupon(couponDTO);
            response.put("success", true);
            response.put("message", "쿠폰이 성공적으로 수정되었습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("쿠폰 수정 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "쿠폰 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 쿠폰 삭제
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCoupon(@RequestParam("couponId") Integer couponId) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            couponService.deleteCoupon(couponId);
            response.put("success", true);
            response.put("message", "쿠폰이 성공적으로 삭제되었습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("쿠폰 삭제 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "쿠폰 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 특정 사용자에게 쿠폰 발급
    @PostMapping("/issue/user")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> issueCouponToUser(
            @RequestParam("couponId") Integer couponId,
            @RequestParam("userId") Integer userId,
            @RequestParam("validUntil") String validUntil) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            LocalDate validUntilDate = LocalDate.parse(validUntil);
            Timestamp validUntilTimestamp = Timestamp.valueOf(validUntilDate.atStartOfDay());
            couponService.issueCouponToUser(couponId, userId, validUntilTimestamp);
            response.put("success", true);
            response.put("message", "쿠폰이 사용자에게 성공적으로 발급되었습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 전체 사용자에게 쿠폰 발급
    @PostMapping("/issue/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> issueCouponToAllUsers(
            @RequestParam("couponId") Integer couponId,
            @RequestParam("validUntil") String validUntil) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin()) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            LocalDate validUntilDate = LocalDate.parse(validUntil);
            Timestamp validUntilTimestamp = Timestamp.valueOf(validUntilDate.atStartOfDay());
            couponService.issueCouponToAllUsers(couponId, validUntilTimestamp);
            response.put("success", true);
            response.put("message", "쿠폰이 전체 사용자에게 성공적으로 발급되었습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "쿠폰 발급 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
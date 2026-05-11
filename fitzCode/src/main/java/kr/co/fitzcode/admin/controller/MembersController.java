package kr.co.fitzcode.admin.controller;

import kr.co.fitzcode.admin.service.MembersService;
import kr.co.fitzcode.common.dto.MemberDetailDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import kr.co.fitzcode.common.enums.UserTier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class MembersController {
    private final MembersService membersService;

    // 필터링된 회원 목록 조회 + 페이지네이션
    @GetMapping
    public String getFilteredMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer role,
            @RequestParam(defaultValue = "recent") String sortOrder,
            @RequestParam(required = false) String keyword,
            Model model) {
        Map<String, Object> data = membersService.getFilteredMembers(page, size, role, sortOrder, keyword);

        model.addAttribute("members", data.get("members"));
        model.addAttribute("totalCount", data.get("totalCount"));
        model.addAttribute("currentPage", data.get("currentPage"));
        model.addAttribute("pageSize", data.get("pageSize"));
        model.addAttribute("role", role);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);

        return "admin/member/members";
    }

    // 회원 상세 정보 조회
    @GetMapping("/{id}")
    public String getMemberDetail(@PathVariable int id, Model model) {
        MemberDetailDTO member = membersService.getMemberDetail(id);
        String tierName = UserTier.getTierName(member.getTierLevel());
        int discountRate = UserTier.getDiscountRate(member.getTierLevel());

        model.addAttribute("member", member);
        model.addAttribute("tierName", tierName);
        model.addAttribute("discountRate", discountRate);
        return "admin/member/memberDetail";
    }

    // 회원 삭제
    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable int id) {
        membersService.deleteMember(id);
        return "redirect:/admin/members";
    }

    // 회원 등급 업데이트
    @PostMapping("/{id}/tier")
    public String updateUserTier(@PathVariable int id, @RequestParam int tierLevel) {
        membersService.updateUserTier(id, tierLevel);
        return "redirect:/admin/members/" + id;
    }

    // 회원 주문 내역을 비동기로 조회
    @GetMapping("/{id}/orders")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getMemberOrders(@PathVariable("id") int userId) {
        MemberDetailDTO member = membersService.getMemberDetail(userId);
        List<OrderDTO> orders = member.getOrders();
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(orders);
    }
}
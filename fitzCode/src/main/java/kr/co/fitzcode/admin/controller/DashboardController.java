package kr.co.fitzcode.admin.controller;

import jakarta.servlet.http.HttpSession;
import kr.co.fitzcode.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String showAdminDashboard(Model model, HttpSession session) {
        // Spring Security에서 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedUserName = authentication.getName(); // 사용자 이름
        String loggedUserProfileImage = "/img/default-profile.png"; // 프로필 이미지 (고정)

        // 세션 활동 갱신
        dashboardService.updateUserActivity(session.getId());

        // 알림 상태 확인 (기본값 false, 실제로는 DB 조회 필요)
        boolean hasNotifications = false;

        // 사용자 정보 모델에 추가
        model.addAttribute("loggedUserName", loggedUserName);
        model.addAttribute("loggedUserProfileImage", loggedUserProfileImage);
        model.addAttribute("hasNotifications", hasNotifications);

        // 1대1 문의 상태별 개수 데이터
        Map<String, Integer> inquiryStatusCounts = dashboardService.getInquiryStatusCounts();
        model.addAttribute("inquiryStatusCounts", inquiryStatusCounts);

        // 주간 신규 가입자 수 데이터
        Map<LocalDate, Integer> weeklyNewUsers = dashboardService.getWeeklyNewUsers();
        List<String> newUserDateLabels = weeklyNewUsers.keySet().stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("M/d")))
                .collect(Collectors.toList());
        List<Integer> newUserCounts = new ArrayList<>(weeklyNewUsers.values());
        model.addAttribute("weeklyNewUserDates", newUserDateLabels);
        model.addAttribute("weeklyNewUserCounts", newUserCounts);

        // 주간 방문자 수 데이터
        Map<LocalDate, Integer> weeklyVisitors = dashboardService.getWeeklyVisitors();
        List<String> visitorDateLabels = weeklyVisitors.keySet().stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("M/d")))
                .collect(Collectors.toList());
        List<Integer> visitorCounts = new ArrayList<>(weeklyVisitors.values());
        model.addAttribute("weeklyVisitorDates", visitorDateLabels);
        model.addAttribute("weeklyVisitorCounts", visitorCounts);

        // KPI 데이터
        model.addAttribute("todayOrdersCount", dashboardService.getTodayOrdersCount());
        model.addAttribute("todayTotalSales", dashboardService.getTodayTotalSales());
        model.addAttribute("cancelReturnsCount", dashboardService.getCancelReturnsCount());
        model.addAttribute("currentVisitors", dashboardService.getCurrentVisitors());

        // 등급별 회원 수 데이터
        Map<Integer, Integer> memberTierCounts = dashboardService.getMemberTierCounts();
        model.addAttribute("memberTierCounts", memberTierCounts);

        return "admin/dashboard/dashboard";
    }

    // 실시간 현재 접속자 수 조회
    @GetMapping("/current-visitors")
    @ResponseBody
    public int getCurrentVisitors(HttpSession session) {
        dashboardService.updateUserActivity(session.getId());
        return dashboardService.getCurrentVisitors();
    }
}
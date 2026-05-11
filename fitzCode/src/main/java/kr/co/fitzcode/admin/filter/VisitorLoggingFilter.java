package kr.co.fitzcode.admin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.fitzcode.admin.mapper.DashboardMapper;
import kr.co.fitzcode.common.dto.VisitorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class VisitorLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(VisitorLoggingFilter.class);
    private static final String VISITOR_ID_COOKIE = "visitorId";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일 유지
    private static final long DUPLICATE_CHECK_INTERVAL = 30 * 60 * 1000; // 30분 (밀리초)
    private final DashboardMapper dashboardMapper;

    private static final Pattern[] SEARCH_ENGINE_PATTERNS = {
            Pattern.compile("https?://(www\\.)?google\\.(com|co\\.kr|.*)"),
            Pattern.compile("https?://(www\\.)?naver\\.com"),
            Pattern.compile("https?://(www\\.)?yahoo\\.com"),
            Pattern.compile("https?://(www\\.)?bing\\.com")
    };

    public VisitorLoggingFilter(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
//        log.info("VisitorLoggingFilter 초기화 - dashboardMapper: {}", dashboardMapper);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (dashboardMapper == null) {
//                log.error("DashboardMapper가 null입니다. 방문자 로그 삽입 불가.");
                filterChain.doFilter(request, response);
                return;
            }

            String pageUrl = request.getRequestURI();
            if (isExcludedResource(pageUrl)) {
//                log.debug("제외된 리소스 요청: {}", pageUrl);
                filterChain.doFilter(request, response);
                return;
            }

            String visitorId = getVisitorIdFromCookie(request);
            if (visitorId == null) {
                visitorId = UUID.randomUUID().toString();
                setVisitorIdCookie(response, visitorId);
//                log.debug("새로운 visitorId 생성: {}", visitorId);
            }

            Long userId = null;
            Timestamp visitTime = new Timestamp(System.currentTimeMillis());
            String referrerUrl = request.getHeader("Referer");
            String userAgent = request.getHeader("User-Agent");
            int deviceType = userAgent.contains("Mobile") ? 1 : 2;
            String ipAddress = request.getRemoteAddr();
            String processedReferrerUrl = processReferrerUrl(referrerUrl);

            VisitorDTO recentLog = dashboardMapper.findRecentVisitLogByVisitorId(visitorId);
//            log.debug("최근 로그 조회 - visitorId: {}, recentLog: {}", visitorId, recentLog);

            if (recentLog == null || (System.currentTimeMillis() - recentLog.getVisitTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) > DUPLICATE_CHECK_INTERVAL) {
//                log.debug("로그 삽입 시도 - visitorId: {}, pageUrl: {}, visitTime: {}", visitorId, pageUrl, visitTime);
                dashboardMapper.insertVisitLog(userId, visitorId, visitTime, pageUrl, processedReferrerUrl, userAgent, deviceType, ipAddress);
//                log.info("방문자 로그 삽입 완료 - visitorId: {}", visitorId);
            } else {
//                log.debug("중복 방문 건너뜀 - visitorId: {}", visitorId);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("VisitorLoggingFilter 오류 발생: ", e);
            throw e;
        }
    }

    private boolean isExcludedResource(String path) {
        // 정적 리소스만 제외
        return path.startsWith("/css/") ||
                path.startsWith("/img/") ||
                path.startsWith("/js/") ||
                path.equals("/favicon.ico");
    }

    private String getVisitorIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> visitorCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> VISITOR_ID_COOKIE.equals(cookie.getName()))
                    .findFirst();
            return visitorCookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }

    private void setVisitorIdCookie(HttpServletResponse response, String visitorId) {
        Cookie cookie = new Cookie(VISITOR_ID_COOKIE, visitorId);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 로컬 테스트 시 false로 설정 true로 하면 https연결에서만 전송됨
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String processReferrerUrl(String referrerUrl) {
        if (referrerUrl == null || referrerUrl.isEmpty()) {
            return null;
        }
        for (Pattern pattern : SEARCH_ENGINE_PATTERNS) {
            if (pattern.matcher(referrerUrl).matches()) {
                return "SEARCH_ENGINE:" + referrerUrl;
            }
        }
        if (referrerUrl.startsWith("http://localhost:8080") || referrerUrl.startsWith("https://localhost:8080")) {
            return null;
        }
        return "EXTERNAL_LINK:" + referrerUrl;
    }
}
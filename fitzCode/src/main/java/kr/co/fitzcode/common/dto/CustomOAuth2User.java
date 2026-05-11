package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.user.service.OAuth2Response;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

@Schema(description = "OAuth2 인증 사용자 정보")
public class CustomOAuth2User implements OAuth2User, AuthenticatedUser {

    @Schema(description = "OAuth2 응답 객체")
    private final OAuth2Response oAuth2Response;

    @Schema(description = "사용자 역할 목록")
    private final List<String> roles;

    @Schema(description = "사용자 ID")
    private final int userId;

    // 생성자: OAuth2 응답, 역할, 사용자 ID를 받아 초기화
    public CustomOAuth2User(OAuth2Response oAuth2Response, List<String> roles, int userId) {
        this.oAuth2Response = oAuth2Response;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.userId = userId;
    }

    // OAuth2User 인터페이스 메서드: 사용자의 속성 맵 반환
    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("birthyear", oAuth2Response.getBirthyear());
        attributes.put("birthday", oAuth2Response.getBirthday());
        attributes.put("email", oAuth2Response.getEmail());
        attributes.put("phonenumber", oAuth2Response.getPhoneNumber());
        attributes.put("username", oAuth2Response.getuserName());
        attributes.put("profileimg", oAuth2Response.getProfileImageUrl());
        attributes.put("provider", oAuth2Response.getProvider());
        attributes.put("providerid", oAuth2Response.getProviderId());
        attributes.put("nickname", oAuth2Response.getNickname());
        attributes.put("userId", userId);
        return attributes;
    }

    // OAuth2User 인터페이스 메서드: 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // OAuth2User 인터페이스 메서드: 사용자 이름 반환
    @Override
    public String getName() {
        return oAuth2Response.getuserName();
    }

    // 소셜 로그인 제공자와 제공자 ID를 조합하여 고유 사용자 이름 생성
    public String getUsername() {
        return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    }

    // AuthenticatedUser 인터페이스 메서드: 사용자 ID 반환
    @Override
    public int getUserId() {
        return userId;
    }

    // 사용자의 역할 목록 리턴
    public List<String> getRoles() {
        return new ArrayList<>(roles);
    }
}
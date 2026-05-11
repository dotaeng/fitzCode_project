package kr.co.fitzcode.common.service;

import kr.co.fitzcode.common.dto.UserDTO;
import kr.co.fitzcode.common.enums.UserRole;
import kr.co.fitzcode.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        UserDTO user = userMapper.getUserByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        logger.debug("User found: {}", user);

        // findRolesInStringByUserId가 [1, 2, 3, 4]를 문자열로 반환
        List<String> roles = userMapper.findRolesInStringByUserId(user.getUserId());
        logger.debug("Loaded roles for user with email {}: {}", email, roles);

        // 문자열을 정수로 파싱 후 UserRole로 매핑
        List<String> authorities = roles.stream()
                .map(Integer::valueOf) // 문자열 "1", "2" 등을 정수로 변환
                .map(roleCode -> UserRole.fromCode(roleCode).getRoleName()) // UserRole로 변환 후 권한 이름 추출
                .collect(Collectors.toList());

        logger.debug("Granting authorities: {}", authorities);

        logger.debug("Returning UserDetails with authorities: {}", authorities);
        return new CustomUserDetails(user, authorities);
    }
}
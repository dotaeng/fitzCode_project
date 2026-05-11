package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum UserRole implements GrantedAuthority {
    USER(1, "사용자", "ROLE_USER"),
    ADMIN(2, "관리자", "ROLE_ADMIN"),
    LOGISTICS(3, "물류담당자", "ROLE_LOGISTICS"),
    INQUIRY(4, "문의담당자", "ROLE_INQUIRY"),
    UNKNOWN(0, "알 수 없음", "ROLE_UNKNOWN");

    private final int code;
    private final String description;
    private final String roleName;

    // 코드 매핑을 위한 Map
    private static final Map<Integer, UserRole> CODE_TO_ROLE;

    static {
        CODE_TO_ROLE = new HashMap<>();
        for (UserRole role : values()) {
            CODE_TO_ROLE.put(role.getCode(), role);
        }
    }

    public static UserRole fromCode(int code) {
        return CODE_TO_ROLE.getOrDefault(code, UNKNOWN);
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "회원 정보")
public class MemberDTO {
    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "이메일 주소")
    private String email;

    @Schema(description = "회원 가입 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "역할 ID")
    private int roleId;

    @Schema(description = "회원 등급")
    private int tierLevel;

    @Schema(description = "카테고리")
    private List<Integer> categoryId;

    // UserRole -> enum으로 변환
//    public UserRole getUserRole() {
//        return UserRole.fromCode(this.roleId);
//    }
}
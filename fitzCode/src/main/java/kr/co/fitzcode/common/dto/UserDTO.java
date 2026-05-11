package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.fitzcode.common.enums.UserTier;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보")
public class UserDTO {
    @Schema(description = "사용자 ID")
    private int userId;

    @NotNull(message = "사용자 이름은 필수입니다.")
    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "닉네임")
    private String nickname;

    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Schema(description = "이메일")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상 최대 16자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "비밀번호는 영문자와 숫자로만 구성되어야 합니다.")
    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Size(min = 8, max = 8, message = "생년월일은 8자로 입력해주세요.")
    @Schema(description = "생년월일")
    private String birthDate;

    @Schema(description = "카카오 ID")
    private String kakaoId;

    @Schema(description = "네이버 ID")
    private String naverId;

    @Schema(description = "프로필 이미지")
    private String profileImage;

    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updatedAt;

    @Schema(description = "역할 ID")
    private int roleId;

    @Schema(description = "회원 등급")
    private int tierLevel;

    public UserTier getTier() {
        return UserTier.fromCode(tierLevel);
    }
}
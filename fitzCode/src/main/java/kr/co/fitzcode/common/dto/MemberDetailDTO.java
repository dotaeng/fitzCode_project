package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "회원 상세 정보")
public class MemberDetailDTO {
    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "이름")
    private String userName;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Schema(description = "생년월일")
    private String birthDate;

    @Schema(description = "카카오 ID")
    private String kakaoId;

    @Schema(description = "네이버 ID")
    private String naverId;

    @Schema(description = "프로필 이미지")
    private String profileImage;

    @Schema(description = "가입 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updatedAt;

    @Schema(description = "등급")
    private int tierLevel;

    @Schema(description = "총 구매 금액")
    private int totalSpent;

    @Schema(description = "등급 업데이트 날짜")
    private LocalDateTime tierUpdatedAt;

    @Schema(description = "기본 주소")
    private AddressDTO defaultAddress;

    @Schema(description = "기본 계좌")
    private AccountDTO defaultAccount;

    @Schema(description = "주문 내역")
    private List<OrderDTO> orders;
}
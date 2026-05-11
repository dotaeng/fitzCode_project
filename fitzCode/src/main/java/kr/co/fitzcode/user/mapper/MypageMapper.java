package kr.co.fitzcode.user.mapper;

import kr.co.fitzcode.common.dto.AccountDTO;
import kr.co.fitzcode.common.dto.CouponDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import kr.co.fitzcode.common.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MypageMapper {
    // 사용자 로그인 정보
    UserDTO getMyInfo(@Param("userId") int userId);

    // 구매내역 & 운송장 정보
    List<OrderDTO> getMypageOrderList(@Param("userId") int userId);

    // 사용자 계좌 정보
    List<AccountDTO> getUserAccount(@Param("userId") int userId);

    // 사용자 쿠폰 정보
    List<CouponDTO> getUserCoupon(@Param("userId") int userId);


    // 회원정보 업데이트
    void updateUserInfo(@Param("userDTO") UserDTO userDTO);

    // 프로필 수정
    void updateProfile(@Param("userDTO") UserDTO userDTO);

    // 사용자 프로필 이미지 가져오기
    String getProfileUrl(@Param("userId") int userId);

    // 사용자 인증
    UserDTO verifyUser(@Param("userDTO") UserDTO userDTO);

    // 계좌 추가
    void insertAccountData(@Param("accountDTO") AccountDTO accountDTO);

    // 계좌 삭제
    void deleteAccount(@Param("accountId") int accountId);
}

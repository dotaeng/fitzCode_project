package kr.co.fitzcode.user.mapper;

import kr.co.fitzcode.common.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(UserDTO dto);
    void insertUserTier(UserDTO dto);

//    void insertUserTier(@Param("nickname") String nickname);
    int emailDuplicate(@Param("email") String email);
    int nicknameDuplicate(@Param("nickname") String nickname);
    int phoneNumberDuplicate(@Param("phoneNumber") String phoneNumber);
    void updatePw(UserDTO dto);
    UserDTO findByEmail(String email);
    // CommonUserController 에서 사용
    UserDTO getUserByEmail(String email);
    List<Integer> getUserRolesByUserId(@Param("userId") int userId);
    UserDTO findByUserNaverId(String naverId);
    void updateUserNaver(UserDTO user);
    UserDTO findByUserKakaoId(String kakaoId);
    String findEmailByNameAndPhoneNumber(@Param("userName") String userName, @Param("phoneNumber") String phoneNumber);

    // 역할 이름을 문자열 리스트로 리턴
    List<String> findRolesInStringByUserId(@Param("userId") int userId);

    // 1. 전화번호로 유저 찾기 (소셜 가입 전 중복/기존 회원 확인용)
    UserDTO findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    // 2. 소셜 계정 ID 연동(업데이트)하기
    void updateUserSocialId(UserDTO user);
}
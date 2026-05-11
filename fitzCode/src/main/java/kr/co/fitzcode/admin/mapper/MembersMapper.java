package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MembersMapper {
    // 모든 회원 목록을 페이징하여 조회
//    List<MemberDTO> getAllMembers(@Param("limit") int limit, @Param("offset") int offset);

    // 전체 회원 수 계산
//    int getTotalMemberCount();

    // 특정 회원의 상세 정보 조회
    MemberDetailDTO getMemberDetail(@Param("userId") int userId);

    // 특정 회원 삭제
    void deleteMember(@Param("userId") int userId);

    // 특정 회원의 등급 업데이트
    void updateUserTier(@Param("userId") int userId, @Param("tierLevel") int tierLevel);

    // 필터링된 회원 수 계산 + 검색기능
    int getTotalFilteredMemberCount(@Param("roleId") Integer roleId, @Param("keyword") String keyword);

    // 필터링된 회원 목록을 페이징하여 조회 + 검색기능
    List<MemberDTO> getFilteredMemberList(@Param("limit") int limit,
                                          @Param("offset") int offset,
                                          @Param("roleId") Integer roleId,
                                          @Param("sortOrder") String sortOrder,
                                          @Param("keyword") String keyword);

    // 사용자의 기본 주소 조회
    AddressDTO getDefaultAddress(@Param("userId") int userId);

    // 사용자의 기본 계좌 조회
    AccountDTO getDefaultAccount(@Param("userId") int userId);

    // 사용자의 주문 내역 조회
    List<OrderDTO> getOrdersByUser(@Param("userId") int userId);

    // 사용자의 총 구매 금액 계산
    Integer getTotalSpent(@Param("userId") int userId);
}
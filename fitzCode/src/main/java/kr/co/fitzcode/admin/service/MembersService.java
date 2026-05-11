package kr.co.fitzcode.admin.service;


import kr.co.fitzcode.common.dto.MemberDetailDTO;

import java.util.Map;

public interface MembersService {
    // 특정 회원의 상세 정보 조회
    MemberDetailDTO getMemberDetail(int userId);

    // 특정 회원 삭제
    void deleteMember(int userId);

    // 특정 회원의 등급 업데이트
    void updateUserTier(int userId, int tierLevel);

    // 필터링된 회원 목록을 페이지네이션과 함께 조회 + 검색기능
    Map<String, Object> getFilteredMembers(int page, int size, Integer roleId, String sortOrder, String keyword);
}
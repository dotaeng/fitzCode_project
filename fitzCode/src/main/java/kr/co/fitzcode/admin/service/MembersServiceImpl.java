package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.MembersMapper;
import kr.co.fitzcode.common.dto.MemberDTO;
import kr.co.fitzcode.common.dto.MemberDetailDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService {
    private final MembersMapper membersMapper;

    // 특정 회원의 상세 정보 조회
    @Override
    public MemberDetailDTO getMemberDetail(int userId) {
        MemberDetailDTO member = membersMapper.getMemberDetail(userId);
        if (member != null) {
            // 기본 주소 설정
            member.setDefaultAddress(membersMapper.getDefaultAddress(userId));
            // 기본 계좌 설정
            member.setDefaultAccount(membersMapper.getDefaultAccount(userId));
            // 주문 내역 설정
            List<OrderDTO> orders = membersMapper.getOrdersByUser(userId);
            member.setOrders(orders != null ? orders : List.of());
            // 총 구매 금액이 0이면 계산
            if (member.getTotalSpent() == 0) {
                int totalSpent = calculateTotalSpent(userId);
                member.setTotalSpent(totalSpent);
            }
        }
        return member;
    }

    // 사용자의 총 구매 금액 계산
    private int calculateTotalSpent(int userId) {
        Integer total = membersMapper.getTotalSpent(userId);
        return total != null ? total : 0;
    }

    // 특정 회원 삭제
    @Override
    public void deleteMember(int userId) {
        membersMapper.deleteMember(userId);
    }

    // 특정 회원의 등급 업데이트
    @Override
    public void updateUserTier(int userId, int tierLevel) {
        membersMapper.updateUserTier(userId, tierLevel);
    }

    // 필터링된 회원 목록을 페이지네이션과 함께 조회 + 검색기능
    @Override
    public Map<String, Object> getFilteredMembers(int page, int size, Integer roleId, String sortOrder, String keyword) {
        int totalCount = membersMapper.getTotalFilteredMemberCount(roleId, keyword); // 검색어 추가
        int offset = (page - 1) * size;
        List<MemberDTO> members = membersMapper.getFilteredMemberList(size, offset, roleId, sortOrder, keyword); // 검색어 추가

        Map<String, Object> response = new HashMap<>();
        response.put("members", members);
        response.put("totalCount", totalCount);
        response.put("currentPage", page);
        response.put("pageSize", size);
        return response;
    }
}
package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.AdminInquiryMapper;
import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.enums.InquiryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminInquiryServiceImpl implements AdminInquiryService {
    private final AdminInquiryMapper inquiryMapper;

    // 답변되지 않은 문의 개수 조회
    @Override
    public int getUnansweredInquiryCount() {
        return inquiryMapper.getUnansweredInquiryCount();
    }

    // 필터링된 총 개수 조회 (카테고리 및 상태 필터링)
    @Override
    public int getTotalInquiryCount(List<Integer> categoryIds, List<Integer> statusCodes) {
        return inquiryMapper.getTotalFilteredInquiryCount(categoryIds, statusCodes);
    }

    // 필터링된 1:1 문의 목록 조회 (페이지네이션)
    @Override
    public List<InquiryDTO> getInquiryList(int page, int size, List<Integer> categoryIds, List<Integer> statusCodes) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;
        return inquiryMapper.getFilteredInquiryList(size, offset, categoryIds, statusCodes);
    }

    // 전체 페이지 수 계산
    @Override
    public int calculateTotalPages(int totalCount, int size) {
        if (size < 1) size = 10;
        return Math.max(1, (int) Math.ceil((double) totalCount / size));
    }

    // 문의 상세 조회
    @Override
    public InquiryDTO getInquiryDetail(int inquiryId) {
        return inquiryMapper.getInquiryDetail(inquiryId);
    }

    // 문의의 상태 업데이트
    @Override
    public void updateInquiryStatus(int inquiryId, int status) {
        inquiryMapper.updateInquiryStatus(inquiryId, status);
    }

    // 문의 카테고리 업데이트
    @Override
    public void updateInquiryCategory(int inquiryId, int category) {
        inquiryMapper.updateInquiryCategory(inquiryId, category);
    }

    // 문의에 답변 추가
    @Override
    public void updateInquiryReply(int inquiryId, String reply) {
        inquiryMapper.updateInquiryReplyAndStatus(inquiryId, reply, InquiryStatus.ANSWERED.getCode());
    }
}
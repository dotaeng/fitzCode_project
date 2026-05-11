package kr.co.fitzcode.admin.service;


import kr.co.fitzcode.common.dto.InquiryDTO;

import java.util.List;

public interface AdminInquiryService {
    // 답변되지 않은 문의 개수 조회
    int getUnansweredInquiryCount();

    // 필터링된 1:1 문의 목록 조회 (카테고리 및 상태 필터링)
    List<InquiryDTO> getInquiryList(int page, int size, List<Integer> categoryIds, List<Integer> statusCodes);

    // 필터링된 1:1 문의 총 개수 조회 (카테고리 및 상태 필터링)
    int getTotalInquiryCount(List<Integer> categoryIds, List<Integer> statusCodes);

    // 전체 페이지 수 계산
    int calculateTotalPages(int totalCount, int size);

    // 문의 상세 조회
    InquiryDTO getInquiryDetail(int inquiryId);

    // 문의 상태 업데이트
    void updateInquiryStatus(int inquiryId, int status);

    // 문의 카테고리 업데이트
    void updateInquiryCategory(int inquiryId, int category);

    // 문의에 대한 답변 추가
    void updateInquiryReply(int inquiryId, String reply);

}
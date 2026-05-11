package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.InquiryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminInquiryMapper {

    // 답변이 없는 문의의 개수를 조회
    int getUnansweredInquiryCount();

    // 전체 문의 목록을 조회 (페이징 처리)
//    List<InquiryDTO> getInquiryList(@Param("limit") int limit, @Param("offset") int offset);

    // 특정 문의의 상세 정보를 조회
    InquiryDTO getInquiryDetail(@Param("inquiryId") int inquiryId);

    // 특정 문의의 상태를 업데이트
    void updateInquiryStatus(@Param("inquiryId") int inquiryId, @Param("status") int status);

    // 특정 문의의 카테고리를 업데이트
    void updateInquiryCategory(@Param("inquiryId") int inquiryId, @Param("category") int category);

    // 특정 문의에 답변과 상태를 업데이트
    void updateInquiryReplyAndStatus(@Param("inquiryId") int inquiryId,
                                     @Param("reply") String reply,
                                     @Param("status") int status);

    // 필터링된 문의의 총 개수를 조회 (카테고리와 상태 코드 기준)
    int getTotalFilteredInquiryCount(@Param("categoryIds") List<Integer> categoryIds,
                                     @Param("statusCodes") List<Integer> statusCodes);

    // 필터링된 문의 목록을 조회 (카테고리와 상태 코드 기준)
    List<InquiryDTO> getFilteredInquiryList(@Param("limit") int limit,
                                            @Param("offset") int offset,
                                            @Param("categoryIds") List<Integer> categoryIds,
                                            @Param("statusCodes") List<Integer> statusCodes);
}
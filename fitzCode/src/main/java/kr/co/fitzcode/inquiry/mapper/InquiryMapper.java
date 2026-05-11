package kr.co.fitzcode.inquiry.mapper;

import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.dto.InquiryImageDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InquiryMapper {
    // 사용자 정보 불러오기
    InquiryDTO getUserOne(int userId);

    // 이미지 저장
    public void saveImages(@Param("imageDTO") InquiryImageDTO imageDTO);

    // 문의 내용 져장
    void saveInquiry(@Param("inquiryDTO") InquiryDTO inquiryDTO);

    // 문의내역 불러오기
    List<InquiryDTO> getInquiryList(@Param("userId") int userId);

    // 문의 상세보기
    InquiryDTO getInquiryDetail(@Param("inquiryId") int inquiryId);

    // 상품 검색
    List<ProductDTO> searchProduct(@Param("userInput") String userInputProductName);

    // 이미지 리스트
    List<InquiryImageDTO> getInquiryImageList(@Param("inquiryId") int inquiryId);

    // 선택된 상품
    ProductDTO selectedProduct(@Param("productId") int productId);

    // 주문 내역
    List<ProductDTO> getOrderList(@Param("userId") int userId);

    // 문의 수정 데이터 저장
    void updateInquiryData(@Param("inquiryDTO") InquiryDTO inquiryDTO);

    // 문의 이미지 삭제
    void deleteInquiryImages(@Param("inquiryId") int inquiryId);

    // 문의 데이터 삭제
    void deleteInquiryData(@Param("inquiryId") int inquiryId);
}

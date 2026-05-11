package kr.co.fitzcode.inquiry.service;

import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.dto.InquiryImageDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InquiryService {
    // 사용자 조회
    InquiryDTO getUserOne(int userId);

    // 문의 데이터 저장
    void insertInquiry(InquiryDTO inquiryDTO, List<MultipartFile> nonEmptyFiles);

    // 개인 문의 내역보기
    List<InquiryDTO> getInquiryList(int userId);

    // 문의 상세보기
    InquiryDTO getInquiryDetail(int inquiryId);

    // 문의 이미지 가져오기
    List<InquiryImageDTO> getInquiryImageList(int inquiryId);

    // 상품 검색
    List<ProductDTO> searchProduct(String userInputProductName);

    // 선택된 상품 가져오기
    ProductDTO selectedProduct(int productId);

    // 주문 내역 가져오기
    List<ProductDTO> getOrderList(int userId);

    // 문의 수정하기
    void updateInquiryData(InquiryDTO inquiryDTO, List<MultipartFile> imageFiles);

    // 문의 삭제
    void deleteInquiryData(int inquiryId);
}

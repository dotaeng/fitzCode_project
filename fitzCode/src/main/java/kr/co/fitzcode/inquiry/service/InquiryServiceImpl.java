package kr.co.fitzcode.inquiry.service;

import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.dto.InquiryImageDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.service.NotificationService;
import kr.co.fitzcode.common.service.S3Service;
import kr.co.fitzcode.inquiry.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private final InquiryMapper inquiryMapper;
    private final S3Service s3Service;
    private final NotificationService notificationService;

    // 사용자 조회
    @Override
    public InquiryDTO getUserOne(int userId) {
        return inquiryMapper.getUserOne(userId);
    }

    // 문의 내용 등록
    @Override
    public void insertInquiry(InquiryDTO inquiryDTO, List<MultipartFile> nonEmptyFiles) {
        // 공통 문의 내용
        inquiryMapper.saveInquiry(inquiryDTO);
        log.info(">>>>>>>>>>>>>>> inquiry_id 확인 {}", inquiryDTO.getInquiryId());
        if (!nonEmptyFiles.isEmpty() && nonEmptyFiles != null) {
            List<String> inquiryImageUrls = saveImages(inquiryDTO.getInquiryId(), nonEmptyFiles);
            inquiryDTO.setImageUrls(inquiryImageUrls);
        }

        // 관리자에게만 알림 저장
        log.info("관리자 알림 발송 시작 - 문의 ID: {}", inquiryDTO.getInquiryId());
        notificationService.sendNotificationToAllAdmins("INQUIRY_CREATED", inquiryDTO);
        log.info("관리자 알림 발송 완료");
    }

    // 개인별 문의 내역 불러오기
    @Override
    public List<InquiryDTO> getInquiryList(int userId) {
        return inquiryMapper.getInquiryList(userId);
    }

    // 문의 상세보기
    @Override
    public InquiryDTO getInquiryDetail(int inquiryId) {
        return inquiryMapper.getInquiryDetail(inquiryId);
    }

    // 상품 찾기
    @Override
    public List<ProductDTO> searchProduct(String userInputProductName) {
        return inquiryMapper.searchProduct(userInputProductName);
    }

    // 검색한 상품 가져오기
    @Override
    public ProductDTO selectedProduct(int productId) {
        return inquiryMapper.selectedProduct(productId);
    }

    // 이미지 리스트 가져오기
    @Override
    public List<InquiryImageDTO> getInquiryImageList(int inquiryId) {
        return inquiryMapper.getInquiryImageList(inquiryId);
    }

    // 주문 내역 불러오기
    @Override
    public List<ProductDTO> getOrderList(int userId) {
        return inquiryMapper.getOrderList(userId);
    }

    // 문의 수정
    @Override
    public void updateInquiryData(InquiryDTO inquiryDTO, List<MultipartFile> nonEmptyFiles) {
        inquiryMapper.updateInquiryData(inquiryDTO);
        if (!nonEmptyFiles.isEmpty() && nonEmptyFiles != null) {
            // S3에 저장되어있는 이미지 삭제
            List<InquiryImageDTO> imageUrls = inquiryMapper.getInquiryImageList(inquiryDTO.getInquiryId());
            if (!imageUrls.isEmpty()) {
                for (InquiryImageDTO fileUrls : imageUrls) {
                    s3Service.deleteFile(fileUrls.getImageUrl());
                    log.info(">>>>>> S3 이미지 url 삭제 : {}", fileUrls.getImageUrl());
                }
            }
            // 저장되어 있던 문의 이미지 삭제 후 (3장에서 2장이 될 수 있으므로)
            inquiryMapper.deleteInquiryImages(inquiryDTO.getInquiryId());
            // 다시 저장하기
            List<String> inquiryImageUrls = saveImages(inquiryDTO.getInquiryId(), nonEmptyFiles);
            inquiryDTO.setImageUrls(inquiryImageUrls);
        }
    }

    // 문의 삭제
    @Override
    public void deleteInquiryData(int inquiryId) {
        inquiryMapper.deleteInquiryData(inquiryId); // 문의 데이터 삭제
        inquiryMapper.deleteInquiryImages(inquiryId); // DB 에서 문의 이미지 삭제
        List<InquiryImageDTO> imageUrls = inquiryMapper.getInquiryImageList(inquiryId);
        if (!imageUrls.isEmpty()) {
            for (InquiryImageDTO fileUrls : imageUrls) {
                s3Service.deleteFile(fileUrls.getImageUrl());
                log.info(">>>>>> S3 이미지 url 삭제 : {}", fileUrls.getImageUrl());
            }
        }
    }

    // 이미지 저장하기
    public List<String> saveImages(int inquiryId, List<MultipartFile> imageFiles) {
        List<String> inquiryImageUrls = s3Service.uploadFiles(imageFiles, "inquiry-images");
        int index = 0;
        for (String inquiryImageUrl : inquiryImageUrls) {
            InquiryImageDTO imageDTO = new InquiryImageDTO();
            imageDTO.setInquiryId(inquiryId);
            imageDTO.setImageUrl(inquiryImageUrl);
            imageDTO.setImageOrder(index++);
            inquiryMapper.saveImages(imageDTO);
        }
        return inquiryImageUrls;
    }
}
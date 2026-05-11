package kr.co.fitzcode.inquiry.controller;

import jakarta.validation.Valid;
import kr.co.fitzcode.common.dto.InquiryDTO;
import kr.co.fitzcode.common.dto.InquiryImageDTO;
import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.util.SecurityUtils;
import kr.co.fitzcode.common.service.NotificationService;
import kr.co.fitzcode.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    // 문의작성 폼으로 이동
    @GetMapping("/inquiryForm")
    public String inquiryForm(Model model) {
        int userId = securityUtils.getUserId();
        InquiryDTO dto = inquiryService.getUserOne(userId);
        model.addAttribute("userDTO", dto);
        return "inquiry/inquiryForm";
    }

    // 문의 데이터 저장
    @PostMapping("/inquiryForm")
    public String inquiryForm(@Valid @ModelAttribute("dto") InquiryDTO inquiryDTO,
                              @RequestParam(value = "files", required = false) List<MultipartFile> imageFiles) {
        // 비어있는 파일을 제외한 이미지 리스트
        List<MultipartFile> nonEmptyFiles = imageFiles.stream()
                .filter(file -> !file.isEmpty())
                .collect(Collectors.toList());
        inquiryDTO.setUserId(securityUtils.getUserId()); // 사용자 ID 설정
        inquiryService.insertInquiry(inquiryDTO, nonEmptyFiles); // 문의 데이터 저장

        // 관리자에게만 알림 저장
        log.info("관리자 알림 발송 시작 - 문의 ID: {}", inquiryDTO.getInquiryId());
        notificationService.sendNotificationToAllAdmins("INQUIRY_CREATED", inquiryDTO);
        log.info("관리자 알림 발송 완료");

        return "redirect:/inquiry/inquiryList";
    }

    // 개인 문의 내역 보기
    @GetMapping("/inquiryList")
    public String inquiryList(Model model) {
        int userId = securityUtils.getUserId();
        List<InquiryDTO> list = inquiryService.getInquiryList(userId);
        model.addAttribute("list", list);
        return "inquiry/inquiryList";
    }

    // 문의 상세보기
    @GetMapping("/detail/{inquiryId}")
    public String detail(@PathVariable("inquiryId") int inquiryId, Model model) {
        InquiryDTO dto = inquiryService.getInquiryDetail(inquiryId); // 문의 내용 가져오기
        List<InquiryImageDTO> list = inquiryService.getInquiryImageList(inquiryId); // 문의 이미지 가져오기
        model.addAttribute("dto", dto);
        model.addAttribute("list", list);
        return "inquiry/inquiryDetail";
    }

    // 상품검색
    @GetMapping("/searchProduct")
    @ResponseBody
    public List<ProductDTO> searchProduct(@RequestParam("userInputProductName") String userInputProductName) {
        List<ProductDTO> list = inquiryService.searchProduct(userInputProductName);
        return list;
    }

    // 선택된 상품 가져오기
    @GetMapping("/selectedProduct")
    @ResponseBody
    public ProductDTO selectedProduct(@RequestParam("productId") int productId) {
        ProductDTO selectedProduct = inquiryService.selectedProduct(productId);
        return selectedProduct;
    }

    // 주문 내역
    @GetMapping("/searchOrderList")
    @ResponseBody
    public List<ProductDTO> searchOrderList(@RequestParam("userId") int userId) {
        List<ProductDTO> orderList = inquiryService.getOrderList(userId);
        log.info(orderList.get(0).getBrand());
        return orderList;
    }

    // 문의 수정폼으로 가기
    @RequestMapping("/modify/{inquiryId}")
    public String modify(@PathVariable("inquiryId") int inquiryId, Model model) {
        int userId = securityUtils.getUserId();
        InquiryDTO userDTO = inquiryService.getUserOne(userId);
        InquiryDTO inquiryDTO = inquiryService.getInquiryDetail(inquiryId);
        log.info(">>>>>>>>> userDTO {}", userDTO.getPhoneNumber());
        log.info(">>>>>>>>> inquiryDTO {}", inquiryDTO.getSubject());
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("inquiryDTO", inquiryDTO);
        return "inquiry/inquiryModify";
    }

    // 수정된 문의 저장
    @PostMapping("/modify")
    public String modify(@ModelAttribute InquiryDTO inquiryDTO,
                         @RequestParam(value = "files", required = false) List<MultipartFile> imageFiles) {
        log.info(">>>>>>>>>>> {} ", inquiryDTO);
        // 비어있는 파일을 제외한 이미지 리스트
        List<MultipartFile> nonEmptyFiles = imageFiles.stream()
                .filter(file -> !file.isEmpty())
                .collect(Collectors.toList());
        inquiryService.updateInquiryData(inquiryDTO, nonEmptyFiles);
        return "redirect:/inquiry/inquiryList";
    }

    // 문의 삭제
    @GetMapping("/delete/{inquiryId}")
    public String delete(@PathVariable("inquiryId") int inquiryId) {
        inquiryService.deleteInquiryData(inquiryId);
        return "redirect:/inquiry/inquiryList";
    }
}
package kr.co.fitzcode.user.service;

import kr.co.fitzcode.common.dto.AccountDTO;
import kr.co.fitzcode.common.dto.CouponDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import kr.co.fitzcode.common.dto.UserDTO;
import kr.co.fitzcode.common.service.S3Service;
import kr.co.fitzcode.user.mapper.MypageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final MypageMapper mypageMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    // 사용자 로그인 정보
    @Override
    public UserDTO getMyInfo(int userId) {
        return mypageMapper.getMyInfo(userId);
    }

    // 구매내역 & 운송장 정보
    @Override
    public List<OrderDTO> getMypageOrderList(int userId) {
        List<OrderDTO> orderDTOList = mypageMapper.getMypageOrderList(userId);
        return orderDTOList;
//        return mypageMapper.getOrderList(userId);
    }

    // 사용자 계좌 정보
    @Override
    public List<AccountDTO> getUserAccount(int userId) {
        return mypageMapper.getUserAccount(userId);
    }

    // 계좌 추가
    @Override
    public void insertAccountData(AccountDTO accountDTO) {
        mypageMapper.insertAccountData(accountDTO);
    }

    // 계좌 삭제
    @Override
    public void deleteAccount(int accountId) {
        mypageMapper.deleteAccount(accountId);
    }


    // 사용자 쿠폰 정보
    @Override
    public List<CouponDTO> getUserCoupon(int userId) {
        return mypageMapper.getUserCoupon(userId);
    }

    // 회원정보 업데이트
    @Override
    public void updateUserInfo(UserDTO userDTO) {
        // 비밀번호 암호화
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        mypageMapper.updateUserInfo(userDTO);
    }

    // 프로필 수정
    @Override
    public void updateProfile(UserDTO userDTO, MultipartFile profileImage) {
        // S3에 저장되어 있는 이미지 삭제
        String fileUrl = mypageMapper.getProfileUrl(userDTO.getUserId()); // 기존의 profileImageUrl 가져오기
        s3Service.deleteFile(fileUrl); // S3 이미지 삭제 후

        String profileImageUrl = s3Service.uploadFile(profileImage, "user-profile"); // S3 저장
        userDTO.setProfileImage(profileImageUrl);
        mypageMapper.updateProfile(userDTO); // DB 저장
    }

    // 회원 인증
    @Override
    public UserDTO verifyUser(UserDTO userDTO) {
        return mypageMapper.verifyUser(userDTO);
    }



}

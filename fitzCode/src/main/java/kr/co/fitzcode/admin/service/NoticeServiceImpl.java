package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.NoticeMapper;
import kr.co.fitzcode.admin.exception.NoticeNotFoundException;
import kr.co.fitzcode.admin.exception.InvalidNoticeIdException;
import kr.co.fitzcode.common.dto.NoticeDTO;
import kr.co.fitzcode.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final S3Service s3Service;

    private static final String NOTICE_IMAGE_FOLDER = "notices/images";
    private static final String NOTICE_ATTACHMENT_FOLDER = "notices/attachments";

    @Override
    public List<NoticeDTO> getNoticesWithPagination(int page, int size) {
        int offset = page * size; // 0-based 인덱스로 변환 (page는 1-based)
        return noticeMapper.selectNoticesWithPagination(offset, size);
    }

    @Override
    public long getTotalNoticeCount() {
        return noticeMapper.countNotices();
    }

    @Override
    public NoticeDTO createNotice(NoticeDTO noticeDTO, MultipartFile imageFile, MultipartFile attachmentFile) {
        if (noticeDTO.getTitle() == null || noticeDTO.getContent() == null) {
            throw new IllegalArgumentException("제목과 내용은 필수입니다.");
        }

        // 대표 이미지 업로드 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile, NOTICE_IMAGE_FOLDER);
            noticeDTO.setImageUrl(imageUrl);
        }

        // 첨부 파일 업로드 처리
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String contentType = attachmentFile.getContentType();
            if (contentType != null && (contentType.equals("application/x-hwp") ||
                    contentType.startsWith("application/vnd.ms-excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                String attachmentUrl = s3Service.uploadFile(attachmentFile, NOTICE_ATTACHMENT_FOLDER);
                noticeDTO.setAttachmentUrl(attachmentUrl);
                noticeDTO.setAttachmentName(attachmentFile.getOriginalFilename());
                noticeDTO.setAttachmentSize(attachmentFile.getSize());
            } else {
                throw new IllegalArgumentException("허용된 파일 형식(HWP, XLS, XLSX, DOC, DOCX)이 아닙니다.");
            }
        }

        // 공지사항 저장 (noticeId가 자동 생성됨)
        noticeMapper.insertNotice(noticeDTO);
        // 생성된 noticeId를 포함한 NoticeDTO 반환
        return noticeDTO; // noticeId가 이미 설정됨 (useGeneratedKeys로 인해)
    }

    @Override
    public NoticeDTO getNoticeById(int noticeId) {
        NoticeDTO notice = noticeMapper.selectNoticeById(noticeId);
        if (notice == null) {
            throw new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId);
        }
        return notice;
    }

    @Override
    @Transactional
    public void updateNotice(NoticeDTO noticeDTO, MultipartFile imageFile, MultipartFile attachmentFile) {
        if (noticeDTO.getNoticeId() <= 0) {
            throw new InvalidNoticeIdException("유효한 공지사항 ID가 필요합니다.");
        }

        NoticeDTO existingNotice = getNoticeById(noticeDTO.getNoticeId());

        // 대표 이미지 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingNotice.getImageUrl() != null && !existingNotice.getImageUrl().isEmpty()) {
                s3Service.deleteFile(existingNotice.getImageUrl());
            }
            String newImageUrl = s3Service.uploadFile(imageFile, NOTICE_IMAGE_FOLDER);
            noticeDTO.setImageUrl(newImageUrl);
        } else {
            noticeDTO.setImageUrl(existingNotice.getImageUrl());
        }

        // 첨부 파일 처리
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            if (existingNotice.getAttachmentUrl() != null && !existingNotice.getAttachmentUrl().isEmpty()) {
                s3Service.deleteFile(existingNotice.getAttachmentUrl());
            }
            String contentType = attachmentFile.getContentType();
            if (contentType != null && (contentType.equals("application/x-hwp") ||
                    contentType.startsWith("application/vnd.ms-excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                String newAttachmentUrl = s3Service.uploadFile(attachmentFile, NOTICE_ATTACHMENT_FOLDER);
                noticeDTO.setAttachmentUrl(newAttachmentUrl);
                noticeDTO.setAttachmentName(attachmentFile.getOriginalFilename());
                noticeDTO.setAttachmentSize(attachmentFile.getSize());
            } else {
                throw new IllegalArgumentException("허용된 파일 형식(HWP, XLS, XLSX, DOC, DOCX)이 아닙니다.");
            }
        } else {
            noticeDTO.setAttachmentUrl(existingNotice.getAttachmentUrl());
            noticeDTO.setAttachmentName(existingNotice.getAttachmentName());
            noticeDTO.setAttachmentSize(existingNotice.getAttachmentSize());
        }

        noticeMapper.updateNotice(noticeDTO);
    }

    @Override
    @Transactional
    public void deleteNotice(int noticeId) {
        if (noticeId <= 0) {
            throw new InvalidNoticeIdException("유효한 공지사항 ID가 필요합니다.");
        }

        NoticeDTO notice = getNoticeById(noticeId);

        // S3에서 파일 삭제
        if (notice.getImageUrl() != null && !notice.getImageUrl().isEmpty()) {
            s3Service.deleteFile(notice.getImageUrl());
        }
        if (notice.getAttachmentUrl() != null && !notice.getAttachmentUrl().isEmpty()) {
            s3Service.deleteFile(notice.getAttachmentUrl());
        }

        // 공지사항 삭제
        noticeMapper.deleteNotice(noticeId);
    }

    @Override
    @Transactional
    public void deleteImage(int noticeId) {
        NoticeDTO notice = getNoticeById(noticeId);
        if (notice.getImageUrl() != null && !notice.getImageUrl().isEmpty()) {
            s3Service.deleteFile(notice.getImageUrl());
            notice.setImageUrl(null);
            noticeMapper.updateNotice(notice);
        }
    }

    @Override
    @Transactional
    public void deleteAttachment(int noticeId) {
        NoticeDTO notice = getNoticeById(noticeId);
        if (notice.getAttachmentUrl() != null && !notice.getAttachmentUrl().isEmpty()) {
            s3Service.deleteFile(notice.getAttachmentUrl());
            notice.setAttachmentUrl(null);
            notice.setAttachmentName(null);
            notice.setAttachmentSize(null);
            noticeMapper.updateNotice(notice);
        }
    }
}
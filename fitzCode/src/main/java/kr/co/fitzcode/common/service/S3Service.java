package kr.co.fitzcode.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    // S3 업로드 (1개만)
    public String uploadFile(MultipartFile file, String folder) {
        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // 여러 개의 파일 업로드
    public List<String> uploadFiles(List<MultipartFile> files, String folder) {
        return files.stream()
                .map(file -> uploadFile(file, folder))
                .collect(Collectors.toList());
    }

    // 기존 파일 삭제
    public void deleteFile(String fileUrl) {
        String key = fileUrl.replace("https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

}
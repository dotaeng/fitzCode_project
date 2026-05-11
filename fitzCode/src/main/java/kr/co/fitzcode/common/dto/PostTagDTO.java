package kr.co.fitzcode.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTagDTO {
    private int postTagId;
    private int postId;
    private int productId;
    private LocalDateTime createdAt;
}
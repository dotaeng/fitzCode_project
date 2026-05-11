package kr.co.fitzcode.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDTO {
    private int postImageId;
    private String postImageUrl;
    private int postId;
    private int postImageOrder;
}
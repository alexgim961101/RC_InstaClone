package com.rising.insta.src.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentLikeEntity {
    private int likeId;
    private int userId;
    private int commentId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private int status;
}

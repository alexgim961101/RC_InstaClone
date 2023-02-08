package com.rising.insta.src.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
    private int commentId;
    private int feedId;
    private int userId;
    private int parentCommentId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private int status;
}

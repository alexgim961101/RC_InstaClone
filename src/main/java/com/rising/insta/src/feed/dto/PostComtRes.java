package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostComtRes {
    private int commentId;
    private int userId;

    private String name;
    private String image;

    private String content;
    private String createdAt;

    private boolean myFeed;
    private boolean myComment;
}

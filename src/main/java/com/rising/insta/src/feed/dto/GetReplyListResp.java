package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetReplyListResp {
    private int parentCommentId;
    private int commentId;
    private int feedId;
    private int userId;
    private String name;
    private String image;
    private String content;
    private String time;
    private boolean myFeed;
    private boolean myComment;
}

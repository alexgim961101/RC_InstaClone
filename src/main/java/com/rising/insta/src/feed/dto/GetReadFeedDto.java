package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetReadFeedDto {
    private int feedId;
    private int userId;

    private String content;
    private int status;
    private String createdAt;
    private String updatedAt;
    private int commentFlag;
    private int likeFlag;
    private String music;
    private String pos;

    private String image;
    private int count;
}

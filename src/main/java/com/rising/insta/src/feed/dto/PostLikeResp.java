package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostLikeResp {
    private int feedId;
    private int userId;
    private String createdAt;
    private int count;
}

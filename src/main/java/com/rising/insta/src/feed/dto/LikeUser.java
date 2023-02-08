package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikeUser {
    private int userId;
    private String nickname;
    private String image;
    private String time;
}

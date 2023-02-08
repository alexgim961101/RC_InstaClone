package com.rising.insta.src.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FollowingUser {
    private int userId;
    private String name;
    private String content;
    private String imageUrl;
}

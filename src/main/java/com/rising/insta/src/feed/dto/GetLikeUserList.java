package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetLikeUserList {
    // 유저 번호
    private int userId;
    // 유저 닉네임
    private String nickname;
    // 유저 이미지
    private String image;
    // 팔로우 상태인지 체크
    private boolean followStatus;
    // 자기 자신인지 체크
    private boolean mySelf;
    // 좋아요 누른 시간
    private String time;
}

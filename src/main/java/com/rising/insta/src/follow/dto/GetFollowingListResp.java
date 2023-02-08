package com.rising.insta.src.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class GetFollowingListResp {
    // 팔로잉 수
    private int count;
    // 현재 유저가 팔로우하고 있는 유저 리스트
    private List<FollowingUser> userList;
}

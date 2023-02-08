package com.rising.insta.src.follow.dto;

import com.rising.insta.src.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class GetFollowerListResp {
    // 팔로워 수
    private int count;
    // 현재 유저를 할로우하고 있는 유저 리스트
    private List<FollowUser> userList;
}

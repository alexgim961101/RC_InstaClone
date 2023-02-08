package com.rising.insta.src.follow;

import com.rising.insta.config.BaseException;
import com.rising.insta.src.follow.dto.FollowUser;
import com.rising.insta.src.follow.dto.FollowingUser;
import com.rising.insta.src.follow.dto.GetFollowerListResp;
import com.rising.insta.src.follow.dto.GetFollowingListResp;
import com.rising.insta.src.follow.entity.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;

    public int checkFollow(int fromUserId, int toUserId) throws BaseException {
        return followRepository.saveFollow(fromUserId, toUserId);
    }

    public int deleteFollow(int fromUserId, int toUserId) throws BaseException {
        return followRepository.deleteFollow(fromUserId, toUserId);
    }

    public GetFollowerListResp readAllFollower(int userId) throws BaseException {
        List<FollowUser> userList = followRepository.readAllFollowerList(userId);
        GetFollowerListResp getFollowerListResp = new GetFollowerListResp(userList.size(), userList);
        return getFollowerListResp;
    }

    public GetFollowingListResp readAllFollowing(int userId) throws BaseException {
        List<FollowingUser> userList = followRepository.readAllFollowingList(userId);
        GetFollowingListResp getFollowingListResp = new GetFollowingListResp(userList.size(), userList);
        return getFollowingListResp;
    }

}

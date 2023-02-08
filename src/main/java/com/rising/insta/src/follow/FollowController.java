package com.rising.insta.src.follow;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.follow.dto.GetFollowerListResp;
import com.rising.insta.src.follow.dto.GetFollowingListResp;
import com.rising.insta.utils.JwtService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/follow")
public class FollowController {

    private final JwtService jwtService;
    private final FollowService followService;

    /**
     * 1. jwt를 이용하여 유저의 idx 추츨 (2001, 2002 에러 발생 가능성)
     * 2. toUserId 검증 (21022 에러 발생 가능성)
     * 2. 서비스 호출
     * */
    @PostMapping("/{toUserId}")
    public BaseResponse<?> follow(@PathVariable Integer toUserId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(toUserId == null || toUserId < 0) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        int result;
        try {
            result = followService.checkFollow(idx, toUserId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String ans = result > 0 ? "팔로우 성공" : "팔로우 실패";
        return new BaseResponse<>(ans);
    }

    /**
     * 1. 유저 idx 추출 (2001, 2002 에러 발생 가능성)
     * 2. toUserId 검증 (21022 에러 발생 가능성)
     * 3. 서비스 호출
     * */
    @DeleteMapping("/{toUserId}")
    public BaseResponse<?> unFollow(@PathVariable Integer toUserId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(toUserId == null || toUserId < 0) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        int result;
        try {
            result = followService.deleteFollow(idx, toUserId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String ans = result > 0 ? "팔로우 취소 성공" : "팔로우 취소 실패";
        return new BaseResponse<>(ans);
    }

    /**
     * 1. jwt토큰을 이용하여 idx 추출 (2001, 2002 에러 발생 가능성)
     * 2. userId 검증 (21022 에러 발생 가능성)
     * 3. 서비스 호출
     * */
    @GetMapping("/to/{userId}")
    public BaseResponse<?> followerList(@PathVariable Integer userId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(userId == null || userId < 0) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        GetFollowerListResp getFollowerListResp = null;
        try {
            getFollowerListResp = followService.readAllFollower(userId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>(getFollowerListResp);
    }

    @GetMapping("/from/{userId}")
    public BaseResponse<?> followingList(@PathVariable Integer userId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(userId == null || userId < 0) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        GetFollowingListResp getFollowingListResp = null;
        try {
            getFollowingListResp = followService.readAllFollowing(userId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(getFollowingListResp);
    }

}

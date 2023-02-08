package com.rising.insta.src.feed;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.src.feed.dto.GetReadFeedDto;
import com.rising.insta.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/search-list")
public class SearchController {
    private final FeedService feedService;
    private final JwtService jwtService;


    /**
     * 홈 화면(2) - 검색? 인기 피드 리스트
     * input : jwt 토큰 (유저인지 아닌지 확인만 하는 용도)
     * error code : 2001 / 2002 / 21022 / 조회 실패 DB 에러
     * output : 개인 게시물 목록 조회 DTO 사용하면 될듯 (GetReadFeedDto)
     * */
    @GetMapping()
    public BaseResponse<List<GetReadFeedDto>> readSearchList() {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        List<GetReadFeedDto> getReadFeedDtos = null;
        try {
            getReadFeedDtos = feedService.AllFeed();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>(getReadFeedDtos);
    }
}

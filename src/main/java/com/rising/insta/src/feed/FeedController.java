package com.rising.insta.src.feed;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.feed.dto.*;
import com.rising.insta.src.feed.entity.Feed;
import com.rising.insta.utils.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/feed")
public class FeedController {

    private final FeedService feedService;
    private final JwtService jwtService;

    /**
     * 0. jwt 토큰 받기
     * 1. 피드 정보 유효성 검사 (사진 개수 1 ~ 10개)
     * 2. feedService 호출
     * 3. feed 엔티티 출력
     * */
    @PostMapping()
    public BaseResponse<?> feedCreate(@ModelAttribute PostFeedUploadDto postFeedUploadDto) {

        // jwt 토큰 받기 (로컬 테스트를 위해 잠시 주석 처리)
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // 사진 갯수 유효성 검사 (multipartfile 형식은 validation 어노테이션 적용 X?)
        try {
            if (postFeedUploadDto.getFileList().isEmpty())
                throw new BaseException(BaseResponseStatus.FEED_IMAGE_OVERFLOW);
            if (postFeedUploadDto.getFileList().size() > 10)
                throw new BaseException(BaseResponseStatus.FEED_IMAGE_OVERFLOW);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // feedService 호출
        Feed feedEntity = null;
        try {
            feedEntity = feedService.saveFeed(idx, postFeedUploadDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getMessage());
        }
        return new BaseResponse<>(feedEntity);
    }

    /**
     * 1. JWT 토큰 확인 (여기서 토큰 존재만 확인하면 된다 -> 회원과 비회원만 분류하면 되기 때문)
     * 2. userId 값을 검증 (음수)
     * 3. 서비스 호출
     * 4. List<GetReadFeedDto> 출력
     * */
    @GetMapping("/{userId}")
    public BaseResponse<?> readAllFeed(@PathVariable Integer userId) {
        // 1. 토큰 존재 확인 (테스트를 위해 주석처리)
        try {
            jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.INVALID_JWT);
        }

        // 2. userId 값 검즘
        if(userId < 0 && userId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        // 3. 서비스 호출
        List<GetReadFeedDto> list = null;
        try {
            list = feedService.AllFeed(userId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getMessage());
        }
        return new BaseResponse<>(list);
    }
    
    /**
     * 1. jwt를 이용하여 현재 사용 유저에 대한 정보를 불러온다.
     * 2. 인수로 넘어온 feedId에 대한 검증
     * 3. 서비스 호출(idx, feedId)
     * 4. 리턴 정보 : 피드, 유저, 좋아요 시간, 피드에 걸린 총 좋아요 수
     * */
    @PostMapping("/{feedId}/likes")
    public BaseResponse<?> like(@PathVariable Integer feedId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(feedId < 0 && feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);


        PostLikeResp postLikeResp = null;
        try {
            postLikeResp = feedService.createLike(idx, feedId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(postLikeResp);
    }

    @PatchMapping("/{feedId}/likes")
    public BaseResponse<?> unLike(@PathVariable Integer feedId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(feedId < 0 && feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        PatchLikeResp patchLikeResp = null;
        try {
            patchLikeResp = feedService.deleteLike(idx, feedId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>(patchLikeResp);
    }

    /**
     * 1.회원인지 체크
     * 2. 해당 피드에 대한 정보 검증
     * 3. 검증이 완료되면 피드에 좋아요를 누른 사람의 정보 티런
     * 4. 필요한 유저 정보 : 유저 번호, 유저 이미지, nickname, createdAt, 팔로우 상태, 본인인지 아닌지 체크
     * */
    @GetMapping("/{feedId}/likes")
    public BaseResponse<List<GetLikeUserList>> userLikeList(@PathVariable Integer feedId) {
        int idx;
        try{
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(feedId < 0 && feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        List<GetLikeUserList> list = null;
        try {
            list = feedService.userList(idx, feedId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(list);
    }

    /**************** 피드 댓글 *****************************************/

    /**
     * input : jwt 토큰(헤더) -> 유저 식별 / 피드 번호 / 내용
     * 에러 코드 : 2001 / 2002 / 21022 / 4000 / 21023 / 41017
     * output : 댓글 번호/ 유저 번호 / 내용 / 시간 / 자기 피드인지 / 자기 댓글인지 / 유저 이름 / 유저 프로필 /
     */

    @PostMapping("/{feedId}/comment")
    public BaseResponse<?> writeComment(@PathVariable Integer feedId, @RequestBody String content) {
        // 1. jwt 토큰 이용 -> 유저 번호 추출
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // 2.feedId, content 검증
        if(feedId < 0 && feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(content.length() == 0) return new BaseResponse<>(BaseResponseStatus.WRITE_COMMENT);


        // 3. 서비스 호출
        PostComtRes postComtRes = null;
        try {
            postComtRes = feedService.saveComment(feedId, idx, 0,content);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        // 4. 결과 리턴
        return new BaseResponse<>(postComtRes);
    }

    /**
     * input : feedId, commentId, jwt토큰
     * 에러 코드 : 2001 / 2002 / 21022 / 4000 / 51003 / 41018
     * output : message(댓글 삭제 성공)
     * */
    @DeleteMapping("/{feedId}/comment/{commentId}")
    public BaseResponse<String> deleteComment(@PathVariable Integer feedId, @PathVariable Integer commentId) {
        // 본인 확인을 위한 유저 번호 추출
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // feedId, commentId 검증
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(commentId < 0 || commentId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        // 서비스 호출
        int result;
        try {
            result = feedService.deleteComment(idx, feedId, commentId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String resp = result == 1 ? "댓글 삭제 성공" : "댓글 삭제 실패";
        return new BaseResponse<>(resp);
    }

    /**
     * input : feedId, commentId, content
     * error code : 2001 / 2002 / 21022 / 4000 / 21023
     * output : 부모 댓글 번호 /댓글 번호 /유저 번호 / 내용 / 시간 / 자기 피드인지 / 자기 댓글인지 / 유저 이름 / 유저 프로필 /
     * */
    @PostMapping("/{feedId}/comment/{commentId}")
    public BaseResponse<PostReplyResp> WriteReply(@PathVariable Integer feedId, @PathVariable Integer commentId, @RequestBody Map<String, String> content) {
        // 본인 확인을 위한 유저 번호 추출
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // feedId, commentId, content 검증
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(commentId < 0 || commentId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(content.get("content").length() == 0) return new BaseResponse<>(BaseResponseStatus.WRITE_COMMENT);

        // 서비스 호출
        PostComtRes postComtRes;
        try {
            postComtRes = feedService.saveComment(feedId, idx, commentId, content.get("content"));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        PostReplyResp postReplyResp = new PostReplyResp(
                commentId,
                postComtRes.getCommentId(),
                idx,
                postComtRes.getName(),
                postComtRes.getImage(),
                postComtRes.getContent(),
                postComtRes.getCreatedAt(),
                postComtRes.isMyFeed(),
                postComtRes.isMyComment()
        );


        return new BaseResponse<>(postReplyResp);
    }

    /**
     * 피드 댓글 리스트 API (/feed/{feedId}/comment)
     * input : jwt(유저 id 출력도 해줘야 됨), feedId -> 둘다 검증 필요
     * error code : 2001 / 2002 / 21022 / 4000
     * output : List<GetCommentListResp> (부모 댓글 번호 = 0인 댓글 , 댓글 번호, 피드 번호 ,유저 번호, 유저 이름, 유저 이미지, 댓글 내용, 시간, 내 피드인지, 내 댓글인지)
     * */
    @GetMapping("/{feedId}/comment")
    public BaseResponse<List<GetCommentListResp>> readAllComment(@PathVariable Integer feedId) {
        // 본인 확인을 위한 유저 번호 추출
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // 검증
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        // 서비스 호출
        List<GetCommentListResp> getCommentListRespList = null;
        try {
            getCommentListRespList = feedService.getCommentList(idx, feedId);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

        // 리턴
        return new BaseResponse<>(getCommentListRespList);
    }

    /**
     * 피드 대댓글 리스트 API (/feed/{feedId}/comment/{commentId})
     * input : jwt(유저 id 출력도 해줘야 됨), feedId, commentId -> 둘다 검증 필요
     * error code : 2001 / 2002 / 21022 / 4000
     * output : List<GetReplyListResp>
     * */
    @GetMapping("/{feedId}/comment/{commentId}")
    public BaseResponse<List<GetReplyListResp>> readAllReply(@PathVariable Integer feedId, @PathVariable Integer commentId) {
        // 본인 확인을 위한 유저 번호 추출
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        // 검증
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(commentId < 0 || commentId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        // 서비스 호출
        List<GetReplyListResp> getReplyListRespList = null;
        try {
            getReplyListRespList = feedService.getReplyList(idx, feedId, commentId);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

        // 리턴
        return new BaseResponse<>(getReplyListRespList);
    }

    /**
     * 피드 목록 조회(자신 피드 + 팔로잉 중인 사람 피드만) API
     * input : jwt -> 유저 식별을 위한 토큰
     * error code : 2001 / 2002 / 21022 / 4000
     * output : feed_id, User(피드 작성 유저), pos ,image, image_count, time ,comment_count
     * */
    @GetMapping()
    public BaseResponse<List<GetFollowingFeedResp>> ReadAllFollowingFeed() {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        List<GetFollowingFeedResp> list = null;
        try {
            list = feedService.getFollowingFeedList(idx);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

        return new BaseResponse<>(list);
    }

    /**
     * 피드 상세 조회 API
     * input : jwt, feedId
     * error code : 2001 / 2002 / 21022 / 4000
     * output : User1, Feed, CommentList
     * */
    @GetMapping("/detail/{feedId}")
    public BaseResponse<?> readFeedDetail(@PathVariable Integer feedId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        GetFeedDetailResp getFeedDetailResp = null;
        try {
            getFeedDetailResp = feedService.getFeedDetail(idx, feedId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }


        return new BaseResponse<>(getFeedDetailResp);
    }

    /**
     * 피드 정보 수정
     * input : jwt(수정 가능한 회원인지 check), content(검증 필요), userId
     * error code : 2001 / 2002 / 21022 / 4000 / 수정 권한이 없습니다.(21025) / 글을 입력해주세요(21024) / 이전 글과 동일합니다.(21026)
     * output : 수정 완료 결과
     * */
    @PatchMapping("/{feedId}")
    public BaseResponse<?> updateFeed(@PathVariable int feedId, @RequestBody PatchFeedReq patchFeedReq) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        if(idx != patchFeedReq.getUserId()) return new BaseResponse<>(BaseResponseStatus.NOT_UPDATE_AUTH);
        String content = patchFeedReq.getNewContent();
        if(content == null || content.equals("")) return new BaseResponse<>(BaseResponseStatus.WRITE_CONTENT);
        String preContent = patchFeedReq.getPreContent();
        if(preContent.equals(content)) return new BaseResponse<>(BaseResponseStatus.EQUAL_PRECONTENT);

        int result = 0;
        try {
            result = feedService.updateFeed(feedId, content);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String res = result == 1 ? "업데이트 성공" : "업데이트 실패";
        return new BaseResponse<>(res);
    }

    /**
     * 피드 삭제 API
     * 1. input : jwt, feedId, userId
     * 2. error code : 2001 / 2002 / 21022 / 4000 / 수정 권한이 없습니다.(21025)
     * 3. output : 삭제 성공 여부 메세지
     * */
    @PatchMapping("/{feedId}/d")
    public BaseResponse<?> removeFeed(@PathVariable Integer feedId, @RequestBody Map<String, Integer> userId) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        if(idx != userId.get("userId")) return new BaseResponse<>(BaseResponseStatus.NOT_UPDATE_AUTH);
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);

        int result = 0;
        try {
            result = feedService.deleteFeed(feedId);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        String res = result == 1 ? "삭제 성공" : "삭제 실패";

        return new BaseResponse<>(res);
    }

    /**
     * 좋아요 수 숨기기 API
     * 1. input : jwt, feedId, flag(0 : 보이기, 1 : 숨기기), user_id(피드 주인) ,preFlag(현재 상태)
     * 2. error code : 2001 / 2002 / 21022 / 4000 / 수정 권한이 없습니다.(21025) / 잘못된 상태값입니다(21027) / 기존 상태와 동일합니다.(21028)
     * 3. output : 숨기기 성공 메세지
     * */
    @PatchMapping("/{feedId}/like/{flag}")
    public BaseResponse<?> manipulateLike(@PathVariable Integer feedId, @PathVariable Integer flag, @RequestBody PatchManiLikeReq patchManiLikeReq) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        int preFlag = patchManiLikeReq.getPreFlag();
        if(idx != patchManiLikeReq.getUserId()) return new BaseResponse<>(BaseResponseStatus.NOT_UPDATE_AUTH);
        if(feedId < 0 || feedId == null) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(flag != 0 && flag != 1) return new BaseResponse<>(BaseResponseStatus.INVALID_FLAG);
        if(flag == preFlag) return new BaseResponse<>(BaseResponseStatus.EQUAL_FLAG);

        int result;
        try {
            if (preFlag == 0) {
                result = feedService.showLikeNumber(feedId, flag);
            } else {
                result = feedService.hideLikeNumber(feedId, flag);
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String res = result == 1 ? "변경 성공" : "변경 실패";
        return new BaseResponse<>(res);
    }

    /**
     * 피드 정보 수정 - 댓글 차단 API
     * 1. input : jwt(회원인지, 피드 댓글 차단 권한이 있는지), feedId(어떤 피드의 댓글을 차단할지), flag(차단할지 헤제할지 )
     * 2. error code : 2001 / 2002 / 21022 / 4000 / 수정 권한이 없습니다.(21025) / 잘못된 상태값입니다(21027) / 기존 상태와 동일합니다.(21028)
     * 3. output : 변경 성공 메세지
     * */
    @PatchMapping("/{feedId}/comment/{flag}")
    public BaseResponse<?> manipulateComment(@PathVariable Integer feedId, @PathVariable Integer flag) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if(feedId == null || feedId < 1) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(flag != 0 && flag != 1) return new BaseResponse<>(BaseResponseStatus.INVALID_FLAG);

        int result;
        try {
            result = feedService.blockComment(idx, feedId, flag);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        String res = result == 1 ? "변경 성공" : "변경 실패";
        return new BaseResponse<>(res);
    }

    /**
     * 피드 댓글 좋아요
     * 1. input : jwt(어떤 유저가 눌렀는지 알기 위해), feedId, commentId(어떤 댓글인지 파악하기 위해), preFlag(현재 좋아요 상태를 어떻게 바꿀지, 0 : 좋아요 안눌려있음  / 1 : 좋아요)
     * 2. error code : 2001 / 2002 / 21022 / 4000 / 기존 상태와 동일합니다.(21028)
     * 3. output : 좋아요 성공 / 실패
     * */
    @PostMapping("/{feedId}/{commentId}/like/s")
    public BaseResponse<String> commentLike(@PathVariable Integer feedId,
                                            @PathVariable Integer commentId,
                                            @RequestBody Map<String, Integer> preFlag) {
        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        if(feedId == null || feedId < 1) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(commentId == null || commentId < 1) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(preFlag.get("preFlag") == 1) return new BaseResponse<>(BaseResponseStatus.EQUAL_FLAG);

        int result = 0;
        try {
            result = feedService.createCommentLike(idx, commentId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }


        String res = result == 1 ? "변경 성공" : "변경 실패";
        return new BaseResponse<>(res);
    }

    /**
     * 피드 댓글 좋아요 취소
     * 1. input : jwt(어떤 유저가 눌렀는지 알기 위해), feedId, commentId(어떤 댓글인지 파악하기 위해), preFlag(현재 좋아요 상태 0 : 좋아요 안눌려있음 / 1 : 좋아요)
     * 2. error code : 2001 / 2002 / 21022 / 4000 / 기존 상태와 동일합니다.(21028)
     * 3. output : 좋아요 취소 성공 / 실패
     * */
    @DeleteMapping("/{feedId}/{commentId}/like/d")
    public BaseResponse<String> removeCommentLike(@PathVariable Integer feedId,
                                                  @PathVariable Integer commentId,
                                                  @RequestBody Map<String, Integer> preFlag) {

        int idx;
        try {
            idx = jwtService.getUserId();
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        if(feedId == null || feedId < 1) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(commentId == null || commentId < 1) return new BaseResponse<>(BaseResponseStatus.INVALID_ACCESS);
        if(preFlag.get("preFlag") == 0) return new BaseResponse<>(BaseResponseStatus.EQUAL_FLAG);

        int result = 0;
        try {
            result = feedService.deleteCommentLike(idx, commentId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }


        String res = result == 1 ? "변경 성공" : "변경 실패";
        return new BaseResponse<>(res);
    }
}

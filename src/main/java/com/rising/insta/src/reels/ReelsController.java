package com.rising.insta.src.reels;

import static com.rising.insta.config.BaseResponseStatus.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.config.Constant;
import com.rising.insta.src.reels.model.DeleteReelsReq;
import com.rising.insta.src.reels.model.GetReelsCommentRes;
import com.rising.insta.src.reels.model.GetReelsRes;
import com.rising.insta.src.reels.model.PatchReelsReq;
import com.rising.insta.src.reels.model.PostReelsCommentReq;
import com.rising.insta.src.reels.model.PostReelsCommentRes;
import com.rising.insta.src.reels.model.PostReelsReq;
import com.rising.insta.src.reels.model.PostReelsRes;
import com.rising.insta.src.user.UserProvider;
import com.rising.insta.utils.JwtService;

@RestController 
@RequestMapping("/app/reels")

public class ReelsController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReelsProvider reelsProvider;
    @Autowired
    private final ReelsService reelsService;
    @Autowired
	private final UserProvider userProvider;
    @Autowired
    private final JwtService jwtService;


    public ReelsController(ReelsProvider reelsProvider, ReelsService reelsService, UserProvider userProvider, JwtService jwtService) {
        this.reelsProvider = reelsProvider;
        this.reelsService = reelsService;
        this.userProvider = userProvider;
        this.jwtService = jwtService; 
    }

    // ******************************************************************************

    /**
     * 릴스 목록 조회 API
     * [GET] /reels
     */
    @ResponseBody 
    @GetMapping("")
    public BaseResponse<List<GetReelsRes>> getReelsList() {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
                return new BaseResponse<>(INVALID_USER_JWT);
            }
        	
        	List<GetReelsRes> getReelsRes = reelsProvider.getReelsList();
        	return new BaseResponse<>(getReelsRes);
        	
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 랜덤한 릴스 조회 API
     * [GET] /reels/random
     */
    @ResponseBody 
    @GetMapping("/random")
    public BaseResponse<List<GetReelsRes>> getRandomReels(@RequestParam(required = false, defaultValue = Constant.RANDOM_REELS_DEFAULT_LIMIT + "") Integer limitCount) {
        try {
        	// 잘못된 입력값이 들어오면 기본값으로 고정
			if (limitCount < 0) {
				limitCount = Constant.RANDOM_REELS_DEFAULT_LIMIT; 
			}
        	
        	// 랜덤한 릴스 조회
        	List<GetReelsRes> getReelsRes = reelsProvider.getRandomReels(limitCount);
        	return new BaseResponse<>(getReelsRes);
        	
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 릴스 정보 조회 API
     * [GET] /reels/:reelsId
     */
    @ResponseBody
    @GetMapping("/{reelsId}")
    public BaseResponse<GetReelsRes> getReels(@PathVariable("reelsId") int reelsId) {
        try {
            GetReelsRes getReelsRes = reelsProvider.getReels(reelsId);
            return new BaseResponse<>(getReelsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    
    /**
     * 릴스 정보 등록 API
     * [POST] /reels
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReelsRes> createReels(@RequestBody PostReelsReq postReelsReq) {
		try {
			// jwt에서 id 추출.
			int userIdByJwt = jwtService.getUserId();
		
			// type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (postReelsReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
	    	
	        
	        // url validation
	        if (postReelsReq.getUrl() == null) {
	        	return new BaseResponse<>(POST_REELS_EMPTY_REELS_URL);
	        }
	        
        	PostReelsRes postReelsRes = reelsService.createReels(postReelsReq);
            return new BaseResponse<>(postReelsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 릴스 정보 변경 API
     * [PATCH] /reels/:reelsId
     */
    @ResponseBody
    @PatchMapping("/{reelsId}")
    public BaseResponse<String> updateReels(@PathVariable("reelsId") int reelsId, @RequestBody PatchReelsReq patchReelsReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (patchReelsReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                
            }
            
            // 수정할 정보가 하나라도 있는지 확인
            if (!patchReelsReq.checkUpdateInfo()) {
            	return new BaseResponse<>(PATCH_REELS_UPDATE_INFO);
            }
        	
        	patchReelsReq.setReelsId(reelsId);
        	reelsService.updateReels(patchReelsReq);

            String result = "릴스 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * 릴스 삭제 API
     * [DELETE] /reels/:reelsId
     */
    @ResponseBody
    @DeleteMapping("/{reelsId}")
    public BaseResponse<String> deleteReels(@PathVariable("reelsId") int reelsId, @RequestBody DeleteReelsReq deleteReelsReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (deleteReelsReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
        	
        	reelsService.deleteReels(reelsId);

            String result = "릴스 정보가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * **********************************************************
     * 댓글
     * **********************************************************
     */
    
    /**
     * 릴스 댓글 등록 API
     * [POST] /reels/:reelsId
     */
    @ResponseBody
    @PostMapping("/{reelsId}")
    public BaseResponse<PostReelsCommentRes> createReelsComment(
    		@PathVariable("reelsId") int reelsId,
    		@RequestBody PostReelsCommentReq postReelsCommentReq) {
		try {
			// jwt에서 id 추출.
			int userIdByJwt = jwtService.getUserId();
		
			// type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (postReelsCommentReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
	    	
	        
	        // comment validation
	        if (postReelsCommentReq.getContent() == null) {
	        	return new BaseResponse<>(POST_REELS_COMMENT_EMPTY_CONTENT);
	        }
	        
        	PostReelsCommentRes postReelsCommentRes = reelsService.createReelsComment(postReelsCommentReq);
            return new BaseResponse<>(postReelsCommentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    
    /**
     * 릴스 댓글 목록 조회 API
     * [GET] /reels/:reelsId
     */
    @ResponseBody
    @GetMapping("/{reelsId}/comment")
    public BaseResponse<List<GetReelsCommentRes>> getReelsComment(@PathVariable("reelsId") int reelsId) {
        try {
            List<GetReelsCommentRes> getReelsCommentsRes = reelsProvider.getReelsComment(reelsId);
            return new BaseResponse<>(getReelsCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}

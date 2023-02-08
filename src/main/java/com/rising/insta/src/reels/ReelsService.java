package com.rising.insta.src.reels;


import static com.rising.insta.config.BaseResponseStatus.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.src.reels.model.PatchReelsReq;
import com.rising.insta.src.reels.model.PostReelsCommentReq;
import com.rising.insta.src.reels.model.PostReelsCommentRes;
import com.rising.insta.src.reels.model.PostReelsReq;
import com.rising.insta.src.reels.model.PostReelsRes;
import com.rising.insta.utils.JwtService;

@Service
@Transactional
public class ReelsService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); 

    private final ReelsDao reelsDao;
    private final ReelsProvider reelsProvider;
    private final JwtService jwtService; 

    @Autowired
    public ReelsService(ReelsDao reelsDao, ReelsProvider reelsProvider, JwtService jwtService) {
        this.reelsDao = reelsDao;
        this.reelsProvider = reelsProvider;
        this.jwtService = jwtService;

    }
    
    // ******************************************************************************
    // 릴스 등록(POST)
    public PostReelsRes createReels(PostReelsReq postReelsReq) throws BaseException {
        try {
            int reelsId = reelsDao.createReels(postReelsReq);
            return new PostReelsRes(reelsId);

        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 릴스 정보 수정 (Patch)
    public void updateReels(PatchReelsReq patchReelsReq) throws BaseException {
        try {
        	if (reelsProvider.checkReelsId(patchReelsReq.getReelsId()) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(REELS_NOT_EXIST_REELS_ID);
        	}
        	
    		int result = reelsDao.updateReels(patchReelsReq);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(MODIFY_REELS_FAIL);
    		}
        	
        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 릴스 정보 삭제 (Delete)
    public void deleteReels(int reelsId) throws BaseException {
    	try {
    		if (reelsProvider.checkReelsId(reelsId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(REELS_NOT_EXIST_REELS_ID);
        	}
    		
    		int result = reelsDao.deleteReels(reelsId);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(DELETE_REELS_FAIL);
    		}
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    /**
     * **********************************************************
     * 댓글
     * **********************************************************
     */

    // 릴스 댓글 등록
	public PostReelsCommentRes createReelsComment(PostReelsCommentReq postReelsCommentReq) throws BaseException {
		try {
			if (reelsProvider.checkReelsId(postReelsCommentReq.getReelsId()) 
					== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(REELS_NOT_EXIST_REELS_ID);
        	}
			
			// 현재 댓글 등록 아이디가 릴스 등록한 유저 아이디인지 확인
			if (reelsProvider.checkReelsUserId(
					postReelsCommentReq.getReelsId(), postReelsCommentReq.getUserId())
					== Constant.ExistQueryResult.EXIST.ordinal()) {
				// 부모 댓글 이 없는 경우 (= 자신이 댓글 다는 경우)
				if (postReelsCommentReq.getParentCommentId() == null) {
					throw new BaseException(POST_REELS_COMMENT_SELF_USER_ID);
				}
			}
			
            int commentId = reelsDao.createReelsComment(postReelsCommentReq);
            return new PostReelsCommentRes(commentId);

        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
	}
}

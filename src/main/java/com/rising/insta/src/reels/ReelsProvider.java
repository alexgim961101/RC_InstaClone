package com.rising.insta.src.reels;

import static com.rising.insta.config.BaseResponseStatus.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.config.Constant.ExistQueryResult;
import com.rising.insta.src.reels.model.GetReelsCommentRes;
import com.rising.insta.src.reels.model.GetReelsRes;
import com.rising.insta.utils.JwtService;

@Service
public class ReelsProvider {

	private final ReelsDao reelsDao;
	private final JwtService jwtService;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ReelsProvider(ReelsDao reelsDao, JwtService jwtService) {
		this.reelsDao = reelsDao;
		this.jwtService = jwtService;
	}
	// ******************************************************************************

	// 해당 reelsId가 존재하는지 확인
	public int checkReelsId(int reelsId) throws BaseException {
		try {
			return reelsDao.checkReelsId(reelsId);
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// Reels 목록 조회
	public List<GetReelsRes> getReelsList() throws BaseException {
		try {
			List<GetReelsRes> getReelsRes = reelsDao.getReelsList();
			return getReelsRes;
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}
	
	// Reels 랜덤 조회
	public List<GetReelsRes> getRandomReels(int limitCount) throws BaseException {
		try {
			List<GetReelsRes> getReelsRes = reelsDao.getRandomReels(limitCount);
			return getReelsRes;
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 해당 reelsId를 갖는 Reels 정보 조회
	public GetReelsRes getReels(int reelsId) throws BaseException {
		try {
        	if (checkReelsId(reelsId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(REELS_NOT_EXIST_REELS_ID);
        	}
			
			GetReelsRes getReelsRes = reelsDao.getReels(reelsId);
			return getReelsRes;
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// TODO 나중에 릴스 등록한 userId인지 확인해서 수정, 삭제 가능하게 현재는 관리자면 통과됨
//	// 릴스를 등록한 userId인지 확인
//	public int checkReelsUserId(int reelsId, int userId) {
//		try {
//			return reelsDao.checkReelsUserId(reelsId, userId);
//		} catch (Exception exception) {
//			logger.error(exception.getMessage());
//			throw new BaseException(DATABASE_ERROR);
//		}
//	}
	
    /**
     * **********************************************************
     * 댓글
     * **********************************************************
     */

	// 릴스 댓글 목록 조회
	public List<GetReelsCommentRes> getReelsComment(int reelsId) throws BaseException {
		try {
        	if (checkReelsId(reelsId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(REELS_NOT_EXIST_REELS_ID);
        	}
			
        	List<GetReelsCommentRes> getReelsCommentRes = reelsDao.getReelsComment(reelsId);
			return getReelsCommentRes;
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 릴스 댓글 유저 확인
	public int checkReelsUserId(int reelsId, int userId) throws BaseException {
		try {
			return reelsDao.checkReelsUserId(reelsId, userId);
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}
}

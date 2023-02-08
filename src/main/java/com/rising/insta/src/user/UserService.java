package com.rising.insta.src.user;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.config.secret.Secret;
import com.rising.insta.src.user.model.PostUserReq;
import com.rising.insta.utils.AES128;
import com.rising.insta.utils.JwtService;
import com.rising.insta.utils.NaverSensV2;
import com.rising.insta.src.user.model.PatchUserReq;
import com.rising.insta.src.user.model.PostUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.rising.insta.config.BaseResponseStatus.*;

import java.util.Random;

@Service
public class UserService {
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final UserDao userDao;
	private final UserProvider userProvider;
	private final JwtService jwtService;

	@Autowired
	public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
		this.userDao = userDao;
		this.userProvider = userProvider;
		this.jwtService = jwtService;

	}
	
	private String createPassword(String password) throws BaseException {
		String pwd = "";
		
		try {
			// 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장
			// ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
			pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(password); // 암호화코드
			
		} catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
			throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
		}
		
		return pwd;
	}

	// ******************************************************************************
	// 회원가입(POST)
	public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
		// 중복 확인: 해당 로그인 Id 체크
		if (userProvider.checkLoginId(postUserReq.getLoginId()) == Constant.ExistQueryResult.EXIST.ordinal()) {
			throw new BaseException(POST_USER_EXISTS_LOGIN_ID);
		}

		String pwd = createPassword(postUserReq.getPassword());
		postUserReq.setPassword(pwd);
		try {
			int userId = userDao.createUser(postUserReq);

			// jwt 발급.
			String jwt = jwtService.createJwt(userId);
			return new PostUserRes(userId, jwt);

		} catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지 보냄
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 유저 정보 수정(Patch)
	public void updateUser(PatchUserReq patchUserReq) throws BaseException {
		try {
			if (userDao.checkUserId(patchUserReq.getUserId()) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(USER_NOT_EXIST_USER_ID);
        	}
			
			if (patchUserReq.getPassword() != null) {
				String pwd = createPassword(patchUserReq.getPassword());
				patchUserReq.setPassword(pwd);
			}
			
			int result = userDao.updateUser(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)

			if (result == Constant.BasicQueryResult.FAIL.ordinal()) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
				throw new BaseException(MODIFY_USER_FAIL);
			}
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 유저 정보 삭제 (Delete)
	public void deleteUser(int userId) throws BaseException {
		try {
			if (userDao.checkUserId(userId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(USER_NOT_EXIST_USER_ID);
        	}
			
			int result = userDao.deleteUser(userId);
			if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(DELETE_USER_FAIL);
    		}
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

}

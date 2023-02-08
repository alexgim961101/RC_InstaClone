package com.rising.insta.src.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.config.Constant;
import com.rising.insta.config.secret.Secret;
import com.rising.insta.src.user.model.PostSocialLoginRes;
import com.rising.insta.src.user.model.GetRecommendUserRes;
import com.rising.insta.src.user.model.GetUserRes;
import com.rising.insta.src.user.model.PostLoginReq;
import com.rising.insta.utils.AES128;
import com.rising.insta.utils.JwtService;
import com.rising.insta.utils.NaverSensV2;
import com.rising.insta.src.user.model.PostLoginRes;
import com.rising.insta.src.user.model.PostSocialLoginReq;
import com.rising.insta.src.user.model.PostUserReq;
import com.rising.insta.src.user.model.User;
import com.rising.insta.src.user.model.UserLoginIdType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.rising.insta.config.BaseResponseStatus.*;

@Service
public class UserProvider {

	private final UserDao userDao;
	private final UserService userService;
	private final JwtService jwtService;
	private final NaverSensV2 naverSensV2;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public UserProvider(UserDao userDao, @Lazy UserService userService, JwtService jwtService
			, NaverSensV2 naverSensV2) {
		this.userDao = userDao;
		this.userService = userService;
		this.jwtService = jwtService;
		this.naverSensV2 = naverSensV2;
	}
	
	// ******************************************************************************

	// 로그인(password 검사)
	public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
		User user = userDao.getPwd(postLoginReq);
		String password;
		try {
			password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword()); // 암호화
			// 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교
		} catch (Exception ignored) {
			throw new BaseException(PASSWORD_DECRYPTION_ERROR);
		}

		if (user.getType() == UserLoginIdType.FACEBOOK.ordinal() || postLoginReq.getPassword().equals(password)) { // 페이스북 로그인 이거나 비밀번호가 일치한다면 userId를 가져온다.
			int userId = user.getUserId();
			String jwt = jwtService.createJwt(userId);
			return new PostLoginRes(userId, jwt);

		} else { // 비밀번호가 다르다면 에러메세지를 출력한다.
			throw new BaseException(FAILED_TO_LOGIN);
		}
	}

	// 해당 login Id가 이미 User Table에 존재하는지 확인
	public int checkLoginId(String loginId) throws BaseException {
		try {
			return userDao.checkLoginId(loginId);
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

    // User들의 정보를 조회
    public List<GetUserRes> getUsers() throws BaseException {
        try {
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
//
//    // 해당 nickname을 갖는 User들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) throws BaseException {
//        try {
//            List<GetUserRes> getUsersRes = userDao.getUsersByNickname(nickname);
//            return getUsersRes;
//        } catch (Exception exception) {
//        	logger.error(exception.getMessage());
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
	
    // 해당 userId를 갖는 User의 정보 조회
    public GetUserRes getUser(int userId) throws BaseException {
        try {
        	if (userDao.checkUserId(userId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(USER_NOT_EXIST_USER_ID);
        	}
        	
            GetUserRes getUserRes = userDao.getUser(userId);
            return getUserRes;
        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

	/**
	 * Social login
	 */
	// 페이스북 로그인
	public PostLoginRes facebookLogin(PostSocialLoginReq postSocialLoginReq) throws BaseException {
		try {
			// 토큰으로 페이스북 API 호출
			PostSocialLoginRes postSocialLoginRes = getFacebookUser(postSocialLoginReq.getAccessToken());

			String loginId = String.valueOf(postSocialLoginRes.getLoginId());
			String userName = postSocialLoginRes.getUserName();
			// TODO 페이스북으로부터 어느정보까지 받아서 처리할지 정리
			String email = postSocialLoginRes.getEmail();
			
			// 로그인 아이디가 존재하지 않으면 새로 생성
			if (checkLoginId(loginId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
				// 랜덤한 비밀번호 생성
				String password = UUID.randomUUID().toString();
				PostUserReq postUserReq = new PostUserReq(loginId, UserLoginIdType.FACEBOOK.ordinal(), userName, password, email);
				// 페이스북ID로 회원가입
				userService.createUser(postUserReq);
			}

			PostLoginReq postLoginReq = new PostLoginReq(loginId, "");

			// 강제 로그인 처리
			// response에 JWT 토큰추가
			return logIn(postLoginReq);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseException(FAILED_TO_LOGIN);
		}
	}
	
	// 엑세스 토큰으로 페이스북 유저 정보 확인
	private PostSocialLoginRes getFacebookUser(String accessToken) throws JsonMappingException, JsonProcessingException {
		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
//			headers.add("Authorization", "Bearer " + accessToken);
//			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		String fields = "id,first_name,last_name,picture,email";
		
		String param = "?fields=" + fields + "&access_token=" + accessToken;

		// HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> facebookUserInfoRequest = new HttpEntity<>(headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange("https://graph.facebook.com/me" + param, HttpMethod.POST,
				facebookUserInfoRequest, String.class);

		// responseBody에 있는 정보를 꺼냄
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);

		Long id = jsonNode.get("id").asLong();
		String email = jsonNode.get("email").asText();
		String firstName = jsonNode.get("first_name").asText();
		String lastName = jsonNode.get("last_name").asText();
		String name = firstName + lastName;

		return new PostSocialLoginRes(id, email, name);
	}
	
	// 추천 유저 목록 조회
	public List<GetRecommendUserRes> getRecommendUsers(int userId, int limitCount) throws BaseException {
		try {
        	if (userDao.checkUserId(userId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(USER_NOT_EXIST_USER_ID);
        	}
        	
        	List<GetRecommendUserRes> getRecommendUsers = userDao.getRecommendUsers(userId, limitCount);
            return getRecommendUsers;
        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        } 
	}

	// userId 가 관리자인지 확인
	public int checkAdminId(int userId) throws BaseException {
		try {
			if (userDao.checkUserId(userId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(USER_NOT_EXIST_USER_ID);
        	}
			
			return userDao.checkAdminId(userId);
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        } 
	}

	// phone 존재하는지 확인
	public int checkPhone(String phone) throws BaseException {
		try {
			return userDao.checkPhone(phone);
		} catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        } 
	}

	// 난수 인증번호 발송
	public String sendRandomMessage(String tel) throws BaseException {
		try {
			Random rand = new Random();
			String numStr = "";
			for (int i = 0; i < 6; i++) {
				String ran = Integer.toString(rand.nextInt(10));
				numStr += ran;
			}
			logger.info("회원가입 문자 인증 => " + numStr);

			naverSensV2.send_msg(tel, numStr);

			return numStr;
		} catch (BaseException e) {
	    	throw new BaseException(e.getStatus());
	    }
	}
	
}

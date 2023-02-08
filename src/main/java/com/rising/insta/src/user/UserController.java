package com.rising.insta.src.user;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponse;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.config.Constant;
import com.rising.insta.src.user.model.GetRecommendUserRes;
import com.rising.insta.src.user.model.GetUserRes;
import com.rising.insta.src.user.model.PatchUserReq;
import com.rising.insta.src.user.model.PostLoginReq;
import com.rising.insta.src.user.model.PostLoginRes;
import com.rising.insta.src.user.model.PostSocialLoginReq;
import com.rising.insta.src.user.model.PostUserReq;
import com.rising.insta.src.user.model.PostUserRes;
import com.rising.insta.src.user.model.UserLoginIdType;
import com.rising.insta.utils.JwtService;
import com.rising.insta.utils.ValidationRegex;

import static com.rising.insta.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/user")
/**
 * 
 * @author 김태훈
 * @updated 22.12.01
 */
public class UserController {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private final UserProvider userProvider;
	@Autowired
	private final UserService userService;
	@Autowired
	private final JwtService jwtService;

	@Autowired
	private final HttpSession session;

	public UserController(UserProvider userProvider, UserService userService, JwtService jwtService,
			HttpSession session) {
		this.userProvider = userProvider;
		this.userService = userService;
		this.jwtService = jwtService;
		this.session = session;
	}


	// ******************************************************************************

	/**
	 * 회원가입 API [POST] /users
	 */
	@ResponseBody
	@PostMapping("/sign-up")
	public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

		// 회원가입 타입이 0 - 이메일인 경우 이메일 필수값 처리
		if (postUserReq.getType() == UserLoginIdType.EMAIL.ordinal() 
				&& postUserReq.getEmail() == null) {
			return new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_EMAIL);
		}
		
		// 회원가입 타입이 1 - 폰인 경우 폰 필수값 처리
		if (postUserReq.getType() == UserLoginIdType.PHONE.ordinal() 
				&& postUserReq.getPhone() == null) {
			return new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_PHONE);
		}
		
		if (postUserReq.getEmail() != null) {
			// 이메일 정규표현 검사
			if (!ValidationRegex.isRegexEmail(postUserReq.getEmail())) {
				return new BaseResponse<>(BaseResponseStatus.POST_USER_INVALID_EMAIL);
			}
		}
		
		if (postUserReq.getPhone() != null) {
			// 핸드폰 정규표현 검사
			if (!ValidationRegex.isRegexPhone(postUserReq.getPhone())) {
				return new BaseResponse<>(BaseResponseStatus.POST_USER_INVALID_PHONE);
			}
		}

		try {
			PostUserRes postUserRes = userService.createUser(postUserReq);
			return new BaseResponse<>(postUserRes);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}

	/**
	 * 로그인 API [POST] /users/login
	 */
	@ResponseBody
	@PostMapping("/login")
	public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
		try {
			if (postLoginReq.getLoginId() == null) {
				return new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_LOING_ID);
			}
			
			if (postLoginReq.getPassword() == null) {
				return new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_PASSWORD);
			}
			
			if (userProvider.checkLoginId(postLoginReq.getLoginId()) 
					== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
				return new BaseResponse<>(BaseResponseStatus.USER_NOT_EXIST_USER_ID);
			}
			
			PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
			return new BaseResponse<>(postLoginRes);
		} catch (BaseException exception) {
			return new BaseResponse<>(exception.getStatus());
		}
	}

    /**
     * 모든 회원 조회 API
     * [GET] /users
     */
    //Query String
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String name) {
        try {
        	// jwt에서 id 추출.
            int userIdByJwt = jwtService.getUserId();
            
            
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
        		return new BaseResponse<>(INVALID_USER_JWT);
            }
        	
        	List<GetUserRes> getUsersRes = userProvider.getUsers();
        	return new BaseResponse<>(getUsersRes);
        	
//            if (name == null) { 
//            }
//            
//            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
//            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//
	
    /**
     * 회원 (개인) 목록 조회 API
     * [GET] /user/:userId
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userId}") 
    public BaseResponse<GetUserRes> getUser(@PathVariable("userId") int userId) {
        try {
            GetUserRes getUserRes = userProvider.getUser(userId);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 정보 변경 API
     * [PATCH] /user/:userId
     */
    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> updateUser(@PathVariable("userId") int userId, @RequestBody PatchUserReq patchUserReq) {
        try {
            // jwt에서 id 추출.
            int userIdByJwt = jwtService.getUserId();
            
            
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 userId와 접근한 유저가 같은지 확인
            	if (userId != userIdByJwt) {
            		return new BaseResponse<>(INVALID_USER_JWT);
            	}
            }

            // 수정할 정보가 하나라도 있는지 확인
            if (!patchUserReq.checkUpdateInfo()) {
            	return new BaseResponse<>(PATCH_USER_UPDATE_INFO);
            }
            
            //같다면 유저 정보 변경
            patchUserReq.setUserId(userId);
            userService.updateUser(patchUserReq);

            String result = "유저 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
	
    // ******************************************************************************
	// Social Login
    // ******************************************************************************
    
	// 페이스북 로그인
	@PostMapping("/social-login")
	public BaseResponse<PostLoginRes> facebookLogin(@RequestBody PostSocialLoginReq postSocialLoginReq) throws JsonProcessingException {
		PostLoginRes postLoginRes;
		try {
			String token = (String) postSocialLoginReq.getAccessToken();
			
			logger.info(String.format("페이스북 엑세스 토큰 : %s", token));
			postLoginRes = userProvider.facebookLogin(postSocialLoginReq);
			return new BaseResponse<PostLoginRes>(postLoginRes);
		} catch (BaseException exception) {
			logger.error(exception.getMessage());
			return new BaseResponse<>((exception.getStatus()));
		}
	}
	
	/**
	 * 추천 유저 목록 조회 API
	 * [GET] /user/:userId/recommend
	 */
	@ResponseBody
	@GetMapping("/{userId}/recommend")
	public BaseResponse<List<GetRecommendUserRes>> getRecommendUser(
			@PathVariable("userId") int userId,
			@RequestParam(required = false, defaultValue = Constant.RECOMMENT_USER_DEFAULT_LIMIT + "") Integer limitCount) {
		try {
			// jwt에서 id 추출.
            int userIdByJwt = jwtService.getUserId();
            // userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
			
			// 잘못된 입력값이 들어오면 기본값으로 고정
			if (limitCount < 0) {
				limitCount = Constant.RECOMMENT_USER_DEFAULT_LIMIT; 
			}
			
			List<GetRecommendUserRes> getRecommendUsersRes = userProvider.getRecommendUsers(userId, limitCount);
			return new BaseResponse<>(getRecommendUsersRes);
		} catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } 
	}
	
	/**
	 * 유저 삭제 API
	 * [DELETE] /user/:userId
	 */
	@ResponseBody
	@DeleteMapping("/{userId}")
	public BaseResponse<String> deleteUser(@PathVariable("userId") int userId) {
		try {
        	// jwt에서 id 추출.
            int userIdByJwt = jwtService.getUserId();
            
            
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
        		return new BaseResponse<>(INVALID_USER_JWT);
            }
            
            userService.deleteUser(userId); 
        	
            String result = "유저 정보가 삭제 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
	}
	
	// ******************************************************************************
	// Sens API - 문자 인증
	// ******************************************************************************
	
	/**
	 * 핸드폰 번호 문자 인증
	 */
	@PostMapping("/auth/phone")
    @ResponseBody
    public BaseResponse<String> phoneAuth(@RequestBody HashMap<String, Object> param) {
		try {
			String phone = (String) param.get("phone");

            if(userProvider.checkPhone(phone)
            		== Constant.ExistQueryResult.EXIST.ordinal())
            	return new BaseResponse<>(AUTH_PHONE_EXIST_PHONE);

	        String code = userProvider.sendRandomMessage(phone);
	        session.setAttribute("rand", code);

	        String result = String.format("%s 번호로 문자 인증이 발송되었습니다.", phone);
	        return new BaseResponse<>(result);
		} catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

	// 핸드폰 번호 인증 완료 처리
    @PostMapping("/auth/phone/check")
    @ResponseBody
    public BaseResponse<String> phoneAuthOk(@RequestBody HashMap<String, Object> param) {
        String rand = (String) session.getAttribute("rand");
        String code = (String) param.get("code");

        logger.info(rand + " : " + code);

        if (!rand.equals(code)) {
            session.removeAttribute("rand");
            return new BaseResponse<>(AUTH_PHONE_INVALID_PHONE_AUTH);
        }

        String result = "핸드폰 번호 인증이 완료되었습니다.";
        return new BaseResponse<>(result);
    }
}

package com.rising.insta.src.shop;

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
import com.rising.insta.src.shop.model.DeleteShopReq;
import com.rising.insta.src.shop.model.GetShopRes;
import com.rising.insta.src.shop.model.PatchShopReq;
import com.rising.insta.src.shop.model.PostShopReq;
import com.rising.insta.src.shop.model.PostShopRes;
import com.rising.insta.src.user.UserProvider;
import com.rising.insta.utils.JwtService;

@RestController 
@RequestMapping("/app/shop")

public class ShopController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ShopProvider shopProvider;
    @Autowired
    private final ShopService shopService;
    @Autowired
	private final UserProvider userProvider;
    @Autowired
    private final JwtService jwtService;


    public ShopController(ShopProvider shopProvider, ShopService shopService, UserProvider userProvider, JwtService jwtService) {
        this.shopProvider = shopProvider;
        this.shopService = shopService;
        this.userProvider = userProvider;
        this.jwtService = jwtService; 
    }

    // ******************************************************************************

    /**
     * 가게 목록 조회 API
     * [GET] /shop
     */
    @ResponseBody 
    @GetMapping("")
    public BaseResponse<List<GetShopRes>> getShopList() {
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
        	
        	List<GetShopRes> getShopRes = shopProvider.getShopList();
        	return new BaseResponse<>(getShopRes);
        	
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 가게 정보 조회 API
     * [GET] /shop/:shopId
     */
    @ResponseBody
    @GetMapping("/{shopId}")
    public BaseResponse<GetShopRes> getShop(@PathVariable("shopId") int shopId) {
        try {
            GetShopRes getShopRes = shopProvider.getShop(shopId);
            return new BaseResponse<>(getShopRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    
    /**
     * 가게 정보 등록 API
     * [POST] /shop
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostShopRes> createShop(@RequestBody PostShopReq postShopReq) {
		try {
			// jwt에서 id 추출.
			int userIdByJwt = jwtService.getUserId();
		
			// type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (postShopReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
	        
	        // name validation
	        if (postShopReq.getName() == null) {
	        	return new BaseResponse<>(POST_SHOP_EMPTY_NAME);
	        }
	        
	        // content validation
	        if (postShopReq.getContent() == null) {
	        	return new BaseResponse<>(POST_SHOP_EMPTY_CONTENT);
	        }

	        PostShopRes postShopRes = shopService.createShop(postShopReq);
            return new BaseResponse<>(postShopRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 가게 정보 변경 API
     * [PATCH] /shop/:shopId
     */
    @ResponseBody
    @PatchMapping("/{shopId}")
    public BaseResponse<String> updateShop(@PathVariable("shopId") int shopId, @RequestBody PatchShopReq patchShopReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (patchShopReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                
            }
            
            // 수정할 정보가 하나라도 있는지 확인
            if (!patchShopReq.checkUpdateInfo()) {
            	return new BaseResponse<>(PATCH_SHOP_UPDATE_INFO);
            }
        	
        	patchShopReq.setShopId(shopId);
        	shopService.updateShop(patchShopReq);

            String result = "가게 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * 가게 삭제 API
     * [DELETE] /shop/:shopId
     */
    @ResponseBody
    @DeleteMapping("/{shopId}")
    public BaseResponse<String> deleteShop(@PathVariable("shopId") int shopId, @RequestBody DeleteShopReq deleteShopReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (deleteShopReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
        	
        	shopService.deleteShop(shopId);

            String result = "가게 정보가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

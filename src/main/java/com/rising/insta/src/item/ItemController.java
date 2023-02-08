package com.rising.insta.src.item;

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
import com.rising.insta.src.item.model.DeleteItemReq;
import com.rising.insta.src.item.model.GetItemRes;
import com.rising.insta.src.item.model.PatchItemReq;
import com.rising.insta.src.item.model.PostItemReq;
import com.rising.insta.src.item.model.PostItemRes;
import com.rising.insta.src.user.UserProvider;
import com.rising.insta.utils.JwtService;

@RestController 
@RequestMapping("/app/item")

public class ItemController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ItemProvider itemProvider;
    @Autowired
    private final ItemService itemService;
    @Autowired
	private final UserProvider userProvider;
    @Autowired
    private final JwtService jwtService;


    public ItemController(ItemProvider itemProvider, ItemService itemService, UserProvider userProvider, JwtService jwtService) {
        this.itemProvider = itemProvider;
        this.itemService = itemService;
        this.userProvider = userProvider;
        this.jwtService = jwtService; 
    }

    // ******************************************************************************

    /**
     * 아이템 목록 조회 API
     * [GET] /item
     */
    @ResponseBody 
    @GetMapping("")
    public BaseResponse<List<GetItemRes>> getItemList() {
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
        	
        	List<GetItemRes> getItemRes = itemProvider.getItemList();
        	return new BaseResponse<>(getItemRes);
        	
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 아이템 정보 조회 API
     * [GET] /item/:itemId
     */
    @ResponseBody
    @GetMapping("/{itemId}")
    public BaseResponse<GetItemRes> getItem(@PathVariable("itemId") int itemId) {
        try {
            GetItemRes getItemRes = itemProvider.getItem(itemId);
            return new BaseResponse<>(getItemRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    
    /**
     * 랜덤한 아이템 조회 API
     * [GET] /item/random
     */
    @ResponseBody 
    @GetMapping("/random")
    public BaseResponse<List<GetItemRes>> getRandomItem(@RequestParam(required = false, defaultValue = Constant.RANDOM_ITEM_DEFAULT_LIMIT + "") Integer limitCount) {
        try {
        	// 잘못된 입력값이 들어오면 기본값으로 고정
			if (limitCount < 0) {
				limitCount = Constant.RANDOM_ITEM_DEFAULT_LIMIT; 
			}
        	
        	// 랜덤한 아이템 조회
        	List<GetItemRes> getItemRes = itemProvider.getRandomItem(limitCount);
        	return new BaseResponse<>(getItemRes);
        	
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * 아이템 정보 등록 API
     * [POST] /item
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostItemRes> createItem(@RequestBody PostItemReq postItemReq) {
		try {
			// jwt에서 id 추출.
			int userIdByJwt = jwtService.getUserId();
		
			// type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (postItemReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
	        
	        // name validation
	        if (postItemReq.getName() == null) {
	        	return new BaseResponse<>(POST_ITEM_EMPTY_NAME);
	        }
	        
	        // price validation
	        if (postItemReq.getContent() == null) {
	        	return new BaseResponse<>(POST_ITEM_EMPTY_PRICE);
	        }
	        
	        // url validation
	        if (postItemReq.getUrl() == null) {
	        	return new BaseResponse<>(POST_ITEM_EMPTY_IMAGE_URL);
	        }
	        
        	PostItemRes postItemRes = itemService.createItem(postItemReq);
            return new BaseResponse<>(postItemRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 아이템 정보 변경 API
     * [PATCH] /item/:itemId
     */
    @ResponseBody
    @PatchMapping("/{itemId}")
    public BaseResponse<String> updateItem(@PathVariable("itemId") int itemId, @RequestBody PatchItemReq patchItemReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (patchItemReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                
            }
            
            // 수정할 정보가 하나라도 있는지 확인
            if (!patchItemReq.checkUpdateInfo()) {
            	return new BaseResponse<>(PATCH_ITEM_UPDATE_INFO);
            }
        	
        	patchItemReq.setItemId(itemId);
        	itemService.updateItem(patchItemReq);

            String result = "아이템 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    
    /**
     * 아이템 삭제 API
     * [DELETE] /item/:itemId
     */
    @ResponseBody
    @DeleteMapping("/{itemId}")
    public BaseResponse<String> deleteItem(@PathVariable("itemId") int itemId, @RequestBody DeleteItemReq deleteItemReq) {
        try {
        	// jwt에서 idx 추출.
            int userIdByJwt = jwtService.getUserId();
    		
            // type이 관리자인지 확인
            // 관리자인 경우 JWT 만으로 넘어감
            if (userProvider.checkAdminId(userIdByJwt) 
            		== Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
            	// 관리자가 아닌 경우 처리
            	// userId와 접근한 유저가 같은지 확인 JWT 탈취 당할 수 있어서
                if (deleteItemReq.getUserId() != userIdByJwt){
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
            }
        	
        	itemService.deleteItem(itemId);

            String result = "아이템 정보가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

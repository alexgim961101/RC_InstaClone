package com.rising.insta.src.shop;


import static com.rising.insta.config.BaseResponseStatus.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.src.shop.model.PatchShopReq;
import com.rising.insta.src.shop.model.PostShopReq;
import com.rising.insta.src.shop.model.PostShopRes;
import com.rising.insta.utils.JwtService;

@Service
@Transactional
public class ShopService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); 

    private final ShopDao shopDao;
    private final ShopProvider shopProvider;
    private final JwtService jwtService; 

    @Autowired
    public ShopService(ShopDao shopDao, ShopProvider shopProvider, JwtService jwtService) {
        this.shopDao = shopDao;
        this.shopProvider = shopProvider;
        this.jwtService = jwtService;

    }
    
    // ******************************************************************************
    // 가게 등록(POST)
    public PostShopRes createShop(PostShopReq postShopReq) throws BaseException {
        try {
            int shopId = shopDao.createShop(postShopReq);
            return new PostShopRes(shopId);

        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 가게 정보 수정 (Patch)
    public void updateShop(PatchShopReq patchShopReq) throws BaseException {
        try {
        	if (shopProvider.checkShopId(patchShopReq.getShopId()) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(SHOP_NOT_EXIST_SHOP_ID);
        	}
        	
    		int result = shopDao.updateShop(patchShopReq);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(MODIFY_SHOP_FAIL);
    		}
        	
        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 가게 정보 삭제 (Delete)
    public void deleteShop(int shopId) throws BaseException {
    	try {
    		if (shopProvider.checkShopId(shopId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(SHOP_NOT_EXIST_SHOP_ID);
        	}
    		
    		int result = shopDao.deleteShop(shopId);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(DELETE_SHOP_FAIL);
    		}
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

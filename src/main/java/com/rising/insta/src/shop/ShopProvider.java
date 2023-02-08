package com.rising.insta.src.shop;

import static com.rising.insta.config.BaseResponseStatus.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.src.shop.model.GetShopRes;
import com.rising.insta.utils.JwtService;

@Service
public class ShopProvider {

	private final ShopDao shopDao;
	private final JwtService jwtService;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ShopProvider(ShopDao shopDao, JwtService jwtService) {
		this.shopDao = shopDao;
		this.jwtService = jwtService;
	}
	// ******************************************************************************

	// 해당 shopId가 존재하는지 확인
	public int checkShopId(int shopId) throws BaseException {
		try {
			return shopDao.checkShopId(shopId);
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// Shop 목록 조회
	public List<GetShopRes> getShopList() throws BaseException {
		try {
			List<GetShopRes> getShopRes = shopDao.getShopList();
			return getShopRes;
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}
	
	// 해당 shopId를 갖는 Shop 정보 조회
	public GetShopRes getShop(int shopId) throws BaseException {
		try {
        	if (checkShopId(shopId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(SHOP_NOT_EXIST_SHOP_ID);
        	}
			
			GetShopRes getShopRes = shopDao.getShop(shopId);
			return getShopRes;
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// TODO 나중에 가게 등록한 userId인지 확인해서 수정, 삭제 가능하게 현재는 관리자면 통과됨
//	// 가게를 등록한 userId인지 확인
//	public int checkShopUserId(int shopId, int userId) {
//		try {
//			return shopDao.checkShopUserId(shopId, userId);
//		} catch (Exception exception) {
//			logger.error(exception.getMessage());
//			throw new BaseException(DATABASE_ERROR);
//		}
//	}

}

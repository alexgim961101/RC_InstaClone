package com.rising.insta.src.item;

import static com.rising.insta.config.BaseResponseStatus.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.src.item.model.GetItemRes;
import com.rising.insta.utils.JwtService;

@Service
public class ItemProvider {

	private final ItemDao itemDao;
	private final JwtService jwtService;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ItemProvider(ItemDao itemDao, JwtService jwtService) {
		this.itemDao = itemDao;
		this.jwtService = jwtService;
	}
	// ******************************************************************************

	// 해당 itemId가 존재하는지 확인
	public int checkItemId(int itemId) throws BaseException {
		try {
			return itemDao.checkItemId(itemId);
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// Item 목록 조회
	public List<GetItemRes> getItemList() throws BaseException {
		try {
			List<GetItemRes> getItemRes = itemDao.getItemList();
			return getItemRes;
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}
	
	// Item 랜덤 조회
	public List<GetItemRes> getRandomItem(int limitCount) throws BaseException {
		try {
			List<GetItemRes> getItemRes = itemDao.getRandomItem(limitCount);
			return getItemRes;
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 해당 itemId를 갖는 Item 정보 조회
	public GetItemRes getItem(int itemId) throws BaseException {
		try {
        	if (checkItemId(itemId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(ITEM_NOT_EXIST_ITEM_ID);
        	}
			
			GetItemRes getItemRes = itemDao.getItem(itemId);
			return getItemRes;
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// TODO 나중에 아이템 등록한 userId인지 확인해서 수정, 삭제 가능하게 현재는 관리자면 통과됨
//	// 아이템를 등록한 userId인지 확인
//	public int checkItemUserId(int itemId, int userId) {
//		try {
//			return itemDao.checkItemUserId(itemId, userId);
//		} catch (Exception exception) {
//			logger.error(exception.getMessage());
//			throw new BaseException(DATABASE_ERROR);
//		}
//	}

}

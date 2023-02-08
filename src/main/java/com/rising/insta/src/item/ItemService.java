package com.rising.insta.src.item;


import static com.rising.insta.config.BaseResponseStatus.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.Constant;
import com.rising.insta.src.item.model.PatchItemReq;
import com.rising.insta.src.item.model.PostItemReq;
import com.rising.insta.src.item.model.PostItemRes;
import com.rising.insta.utils.JwtService;

@Service
@Transactional
public class ItemService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); 

    private final ItemDao itemDao;
    private final ItemProvider itemProvider;
    private final JwtService jwtService; 

    @Autowired
    public ItemService(ItemDao itemDao, ItemProvider itemProvider, JwtService jwtService) {
        this.itemDao = itemDao;
        this.itemProvider = itemProvider;
        this.jwtService = jwtService;

    }
    
    // ******************************************************************************
    // 아이템 등록(POST)
    public PostItemRes createItem(PostItemReq postItemReq) throws BaseException {
        try {
            int itemId = itemDao.createItem(postItemReq);
            return new PostItemRes(itemId);

        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 아이템 정보 수정 (Patch)
    public void updateItem(PatchItemReq patchItemReq) throws BaseException {
        try {
        	if (itemProvider.checkItemId(patchItemReq.getItemId()) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(ITEM_NOT_EXIST_ITEM_ID);
        	}
        	
    		int result = itemDao.updateItem(patchItemReq);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(MODIFY_ITEM_FAIL);
    		}
        	
        } catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
        	logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 아이템 정보 삭제 (Delete)
    public void deleteItem(int itemId) throws BaseException {
    	try {
    		if (itemProvider.checkItemId(itemId) == Constant.ExistQueryResult.NOT_EXIST.ordinal()) {
        		throw new BaseException(ITEM_NOT_EXIST_ITEM_ID);
        	}
    		
    		int result = itemDao.deleteItem(itemId);
    		if (result == Constant.BasicQueryResult.FAIL.ordinal()) {
    			throw new BaseException(DELETE_ITEM_FAIL);
    		}
		} catch (BaseException e) {
        	throw new BaseException(e.getStatus());
        } catch (Exception exception) {
			logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

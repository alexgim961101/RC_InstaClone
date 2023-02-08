package com.rising.insta.src.item;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rising.insta.src.item.model.GetItemRes;
import com.rising.insta.src.item.model.PatchItemReq;
import com.rising.insta.src.item.model.PostItemReq;

@Repository
public class ItemDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    // 아이템 등록
    public int createItem(PostItemReq postItemReq) {
        String createItemQuery = "insert into Item (shop_id, name, content, price, url) VALUES (?,?,?,?,?)";
        Object[] createItemParams = new Object[]{
        		postItemReq.getShopId(),
        		postItemReq.getName(), 
        		postItemReq.getContent(), 
        		postItemReq.getPrice(), 
        		postItemReq.getUrl()
        }; 
        
        this.jdbcTemplate.update(createItemQuery, createItemParams);

        String lastInsertIdQuery = "select last_insert_id()"; 
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
    
    // itemId 확인
    public int checkItemId(int itemId) {
        String checkItemIdQuery = "select exists(select item_id from Item where item_id = ? and status != 1)"; 
        Object checkItemParams = itemId;
        return this.jdbcTemplate.queryForObject(checkItemIdQuery,
                int.class,
                checkItemParams);
    }

    // 아이템 정보 변경
    public int updateItem(PatchItemReq patchItemReq) {
        
        String update = "update Item set ";
        String set = "";
        String where = "where item_id = ? and status != 1";
        
        List<Object> list = new ArrayList<>();

        if (patchItemReq.getName() != null) {
        	set += "name = ?, ";
        	list.add(patchItemReq.getName());
        }
        if (patchItemReq.getContent() != null) {
        	set += "content = ?, ";
        	list.add(patchItemReq.getContent());
        }
        if (patchItemReq.getUrl() != null) {
        	set += "url = ?, ";
        	list.add(patchItemReq.getUrl());
        }
        if (patchItemReq.getPrice() != null) {
        	set += "price = ?, ";
        	list.add(patchItemReq.getPrice());
        }
        if (patchItemReq.getUrl() != null) {
        	set += "url = ?, ";
        	list.add(patchItemReq.getUrl());
        }
        
        // 마지막 column , 제거
        set = set.substring(0, set.lastIndexOf(",")) + " ";
        
        list.add(patchItemReq.getItemId());
        
		String updateItemQuery = update + set + where;
        Object[] updateItemParams = list.toArray();
        

        return this.jdbcTemplate.update(updateItemQuery, updateItemParams); 
    }

    // Item 테이블에 존재하는 전체 아이템들 정보 조회
    public List<GetItemRes> getItemList() {
        String getItemQuery = "select * from Item where status != 1";
        return this.jdbcTemplate.query(getItemQuery,
                (rs, rowNum) -> new GetItemRes(
                        rs.getInt("item_id"),
                        rs.getInt("shop_id"),
                        
                        rs.getString("name"),
                        rs.getString("content"),
                        rs.getInt("price"),
                        rs.getString("url"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ) 
        ); 
    }

    // 해당 itemId를 갖는 아이템 조회
    public GetItemRes getItem(int itemId) {
        String getItemQuery = "select * from Item where item_id = ? and status != 1"; 
        int getItemParams = itemId;
        return this.jdbcTemplate.queryForObject(getItemQuery,
                (rs, rowNum) -> new GetItemRes(
                		rs.getInt("item_id"),
                        rs.getInt("shop_id"),
                        
                        rs.getString("name"),
                        rs.getString("content"),
                        rs.getInt("price"),
                        rs.getString("url"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getItemParams); 
    }
    
    public List<GetItemRes> getRandomItem(int limitCount) {
    	String getRandomItemQuery = "select * from Item"
    			+ " where status != 1"
    			+ " order by rand() limit ?";
    	int getRandomItemParams = limitCount;
        return this.jdbcTemplate.query(getRandomItemQuery,
                (rs, rowNum) -> new GetItemRes(
                		rs.getInt("item_id"),
                        rs.getInt("shop_id"),
                        
                        rs.getString("name"),
                        rs.getString("content"),
                        rs.getInt("price"),
                        rs.getString("url"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getRandomItemParams
        ); 
	}
    
    // 아이템 삭제
    public int deleteItem(int itemId) {
    	String deleteItemQuery = "update Item set status = 1 where item_id = ?";
    	int deleteItemParams = itemId;
    	return this.jdbcTemplate.update(deleteItemQuery, deleteItemParams); 
    }

	
}

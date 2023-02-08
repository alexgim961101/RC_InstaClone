package com.rising.insta.src.shop;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rising.insta.src.shop.model.GetShopRes;
import com.rising.insta.src.shop.model.PatchShopReq;
import com.rising.insta.src.shop.model.PostShopReq;

@Repository
public class ShopDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    // 가게 등록
    public int createShop(PostShopReq postShopReq) {
        String createShopQuery = "insert into Shop (name, icon_url, content) VALUES (?,?,?)";
        Object[] createShopParams = new Object[]{
        		postShopReq.getName(), 
        		postShopReq.getIconUrl(),
        		postShopReq.getContent() 
        }; 
        
        this.jdbcTemplate.update(createShopQuery, createShopParams);

        String lastInsertIdQuery = "select last_insert_id()"; 
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
    
    // shopId 확인
    public int checkShopId(int shopId) {
        String checkShopIdQuery = "select exists(select shop_id from Shop where shop_id = ? and status != 1)"; 
        Object checkShopParams = shopId;
        return this.jdbcTemplate.queryForObject(checkShopIdQuery,
                int.class,
                checkShopParams);
    }

    // 가게 정보 변경
    public int updateShop(PatchShopReq patchShopReq) {
        
        String update = "update Shop set ";
        String set = "";
        String where = "where shop_id = ? and status != 1";
        
        List<Object> list = new ArrayList<>();

        if (patchShopReq.getName() != null) {
        	set += "name = ?, ";
        	list.add(patchShopReq.getName());
        }
        if (patchShopReq.getIconUrl() != null) {
        	set += "icon_url = ?, ";
        	list.add(patchShopReq.getIconUrl());
        }
        if (patchShopReq.getContent() != null) {
        	set += "content = ?, ";
        	list.add(patchShopReq.getContent());
        }
        
        // 마지막 column , 제거
        set = set.substring(0, set.lastIndexOf(",")) + " ";
        
        list.add(patchShopReq.getShopId());
        
		String updateShopQuery = update + set + where;
        Object[] updateShopParams = list.toArray();
        

        return this.jdbcTemplate.update(updateShopQuery, updateShopParams); 
    }

    // Shop 테이블에 존재하는 전체 가게들 정보 조회
    public List<GetShopRes> getShopList() {
        String getShopQuery = "select * from Shop where status != 1";
        return this.jdbcTemplate.query(getShopQuery,
                (rs, rowNum) -> new GetShopRes(
                        rs.getInt("shop_id"),
                        
                        rs.getString("name"),
                        rs.getString("icon_url"),
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ) 
        ); 
    }

    // 해당 shopId를 갖는 가게 조회
    public GetShopRes getShop(int shopId) {
        String getShopQuery = "select * from Shop where shop_id = ? and status != 1"; 
        int getShopParams = shopId;
        return this.jdbcTemplate.queryForObject(getShopQuery,
                (rs, rowNum) -> new GetShopRes(
                		rs.getInt("shop_id"),
                        
                        rs.getString("name"),
                        rs.getString("icon_url"),
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getShopParams); 
    }
    
    // 가게 삭제
    public int deleteShop(int shopId) {
    	String deleteShopQuery = "update Shop set status = 1 where shop_id = ?";
    	int deleteShopParams = shopId;
    	return this.jdbcTemplate.update(deleteShopQuery, deleteShopParams); 
    }

	
}

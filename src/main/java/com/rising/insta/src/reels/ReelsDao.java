package com.rising.insta.src.reels;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rising.insta.src.reels.model.GetReelsCommentRes;
import com.rising.insta.src.reels.model.GetReelsRes;
import com.rising.insta.src.reels.model.PatchReelsReq;
import com.rising.insta.src.reels.model.PostReelsCommentReq;
import com.rising.insta.src.reels.model.PostReelsReq;

@Repository
public class ReelsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    // 릴스 등록
    public int createReels(PostReelsReq postReelsReq) {
        String createReelsQuery = "insert into Reels (user_id, url, content) VALUES (?,?,?)";
        Object[] createReelsParams = new Object[]{
        		postReelsReq.getUserId(),
        		postReelsReq.getUrl(), 
        		postReelsReq.getContent()
        }; 
        
        this.jdbcTemplate.update(createReelsQuery, createReelsParams);

        String lastInsertIdQuery = "select last_insert_id()"; 
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
    
    // reelsId 확인
    public int checkReelsId(int reelsId) {
        String checkReelsIdQuery = "select exists(select reels_id from Reels where reels_id = ? and status != 1)"; 
        Object checkReelsParams = reelsId;
        return this.jdbcTemplate.queryForObject(checkReelsIdQuery,
                int.class,
                checkReelsParams);
    }

    // 릴스 정보 변경
    public int updateReels(PatchReelsReq patchReelsReq) {
        
        String update = "update Reels set ";
        String set = "";
        String where = "where reels_id = ? and status != 1";
        
        List<Object> list = new ArrayList<>();

        if (patchReelsReq.getUrl() != null) {
        	set += "url = ?, ";
        	list.add(patchReelsReq.getUrl());
        }
        if (patchReelsReq.getContent() != null) {
        	set += "content = ?, ";
        	list.add(patchReelsReq.getContent());
        }
        
        // 마지막 column , 제거
        set = set.substring(0, set.lastIndexOf(",")) + " ";
        
        list.add(patchReelsReq.getReelsId());
        
		String updateReelsQuery = update + set + where;
        Object[] updateReelsParams = list.toArray();
        

        return this.jdbcTemplate.update(updateReelsQuery, updateReelsParams); 
    }

    // Reels 테이블에 존재하는 전체 릴스들 정보 조회
    public List<GetReelsRes> getReelsList() {
        String getReelsQuery = "select * from Reels where status != 1";
        return this.jdbcTemplate.query(getReelsQuery,
                (rs, rowNum) -> new GetReelsRes(
                        rs.getInt("reels_id"),
                        rs.getInt("user_id"),
                        
                        rs.getString("url"),
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ) 
        ); 
    }

    // 해당 reelsId를 갖는 릴스 조회
    public GetReelsRes getReels(int reelsId) {
        String getReelsQuery = "select * from Reels where reels_id = ? and status != 1"; 
        int getReelsParams = reelsId;
        return this.jdbcTemplate.queryForObject(getReelsQuery,
                (rs, rowNum) -> new GetReelsRes(
                		rs.getInt("reels_id"),
                        rs.getInt("user_id"),
                        
                        rs.getString("url"),
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getReelsParams); 
    }
    
    public List<GetReelsRes> getRandomReels(int limitCount) {
    	String getRandomReelsQuery = "select * from Reels"
    			+ " where status != 1"
    			+ " order by rand() limit ?";
    	int getRandomReelsParams = limitCount;
        return this.jdbcTemplate.query(getRandomReelsQuery,
                (rs, rowNum) -> new GetReelsRes(
                        rs.getInt("reels_id"),
                        rs.getInt("user_id"),
                        
                        rs.getString("url"),
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getRandomReelsParams
        ); 
	}
    
    // 릴스 삭제
    public int deleteReels(int reelsId) {
    	String deleteReelsQuery = "update Reels set status = 1 where reels_id = ?";
    	int deleteReelsParams = reelsId;
    	return this.jdbcTemplate.update(deleteReelsQuery, deleteReelsParams); 
    }
    
    // 릴스 댓글 등록
	public int createReelsComment(PostReelsCommentReq postReelsCommentReq) {
		String createReelsCommentQuery = "insert into ReelsComment (reels_id, user_id, parent_comment_id, content) VALUES (?,?,?,?)";
        Object[] createReelsCommentParams = new Object[]{
        		postReelsCommentReq.getReelsId(),
        		postReelsCommentReq.getUserId(),
        		postReelsCommentReq.getParentCommentId(),
        		postReelsCommentReq.getContent()
        }; 
        
        this.jdbcTemplate.update(createReelsCommentQuery, createReelsCommentParams);

        String lastInsertIdQuery = "select last_insert_id()"; 
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
	}

	// 릴스 댓글 목록 조회
	public List<GetReelsCommentRes> getReelsComment(int reelsId) {
		String getReelsQuery = "select \n"
				+ "	rcom.comment_id \n"
				+ "    , rcom.parent_comment_id \n"
				+ "    , reels_id \n"
				+ "    , user_id \n"
				+ "    , fnc.level \n"
				+ "	, case when level - 1 > 0 then concat(concat(repeat('', level - 1),''), rcom.content) \n"
				+ "			else rcom.content \n"
				+ "		end as content \n"
				+ "    , rcom.createdAt \n"
				+ "    , rcom.updatedAt \n"
				+ "    , rcom.status \n"
				+ "from \n"
				+ "	(select fnc_hierarchi() as id, @level as level, @ROWNUM := @ROWNUM + 1 AS ROWNUM \n"
				+ "		from (select @start_with:=0, @id:=@start_with, @level:=0) vars \n"
				+ "        join ReelsComment \n"
				+ "        , (SELECT @ROWNUM := 0) TMP \n"
				+ "        where @id is not null) fnc \n"
				+ "join ReelsComment rcom on fnc.id = rcom.comment_id \n"
				+ "where rcom.reels_id = ? \n"
				+ "	and rcom.status != 1 \n"
				+ "order by fnc.ROWNUM"; 
        int getReelsParams = reelsId;
        return this.jdbcTemplate.query(getReelsQuery,
                (rs, rowNum) -> new GetReelsCommentRes(
                		rs.getInt("comment_id"),
                		rs.getInt("parent_comment_id"),
                		rs.getInt("reels_id"),
                        rs.getInt("user_id"),

                        rs.getInt("level"),
                        
                        rs.getString("content"),
                        
                        rs.getString("status"),
                        rs.getTimestamp("updatedAt").toLocalDateTime(),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ),
                getReelsParams); 
	}

	// 릴스 댓글 유저 확인
	public int checkReelsUserId(int reelsId, int userId) {
		String checkReelsUserIdQuery = "select exists(select reels_id from Reels where reels_id = ? and user_id = ? and status != 1)"; 
        Object[] checkReelsUserIdParams = new Object[] { reelsId, userId };
        return this.jdbcTemplate.queryForObject(checkReelsUserIdQuery,
                int.class,
                checkReelsUserIdParams);
	}
	
}

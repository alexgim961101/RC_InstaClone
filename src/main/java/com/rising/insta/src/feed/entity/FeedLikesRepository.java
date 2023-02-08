package com.rising.insta.src.feed.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.feed.dto.GetLikeUserList;
import com.rising.insta.src.feed.dto.LikeUser;
import com.rising.insta.src.feed.dto.PatchLikeResp;
import com.rising.insta.src.feed.dto.PostLikeResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FeedLikesRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * 1. 기존에 같은 정보가 있는지 체크
     * 2. Like 정보 저장
     * 3. 좋아요 누른 피드의 총 좋아요 수 출력
     * */
    public PostLikeResp saveLike(int idx, int feedId) throws BaseException {
        String query0 = "SELECT like_status FROM FeedLike WHERE feed_id = ? AND user_id = ?";
        Integer integer = null;
        try {
            integer = this.jdbcTemplate.queryForObject(query0, Integer.class, feedId, idx);
        } catch (Exception e){
            integer = -1;
        }
        if(integer != -1){
            String query1 = "UPDATE FeedLike SET like_status = 0 WHERE feed_id = ? AND user_id = ?";
            Object[] params = new Object[]{feedId, idx};
            try {
                this.jdbcTemplate.update(query1, params);
            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        } else {
            String query1 = "SELECT count(*) FROM FeedLike WHERE feed_id = ? AND user_id = ? AND like_status = 0";
            if (this.jdbcTemplate.queryForObject(query1, Integer.class, feedId, idx) != 0)
                throw new BaseException(BaseResponseStatus.LIKE_EXIST);

            String query2 = "INSERT INTO FeedLike (feed_id, user_id, createdAt) VALUES (?, ?, now());";
            Object[] params = new Object[]{feedId, idx};
            try {
                this.jdbcTemplate.update(query2, params);
            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
        }
        String query3 = "SELECT count(user_id) FROM FeedLike WHERE feed_id = ? AND like_status = 0";

        int count;
        try {
            count = this.jdbcTemplate.queryForObject(query3, Integer.class, feedId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        String query4 = "SELECT createdAt FROM FeedLike WHERE user_id = ? AND feed_id = ? AND like_status = 0";
        String createdAt = this.jdbcTemplate.queryForObject(query4, String.class, idx, feedId);

        return new PostLikeResp(feedId, idx, createdAt, count);

    }

    public PatchLikeResp patchLike(int idx, int feedId) throws BaseException {
        String query1 = "SELECT count(*) FROM FeedLike WHERE feed_id = ? AND user_id = ? AND like_status = 0";
        if(this.jdbcTemplate.queryForObject(query1, Integer.class, feedId, idx) == 0) throw new BaseException(BaseResponseStatus.LIKE_NOT_EXIST);


        String query2 = "UPDATE FeedLike SET like_status = 1 WHERE feed_id = ? AND user_id = ?";
        Object[] params = new Object[]{feedId, idx};
        try {
            this.jdbcTemplate.update(query2, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        String query3 = "SELECT count(user_id) FROM FeedLike WHERE feed_id = ? AND like_status = 0";
        int count;
        try {
            count = this.jdbcTemplate.queryForObject(query3, Integer.class, feedId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        return new PatchLikeResp(feedId, idx, count);
    }

    public List<LikeUser> getUserLikeList(int feedId) throws BaseException {
        String query = "SELECT F.user_id, U.name, U.image_url, F.createdAt FROM FeedLike F INNER JOIN User U ON F.user_id = U.user_id WHERE feed_id = ? AND like_status = 0";
        try {
            return this.jdbcTemplate.query(query, (rs, count) -> new LikeUser(
                    rs.getInt("F.user_id"),
                    rs.getString("U.name"),
                    rs.getString("U.image_url"),
                    rs.getString("F.createdAt")
                    ),
                    feedId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkFollow(int userId, int targetUserId) {
        String query = "SELECT count(*) FROM Follow WHERE to_user_id = ? AND from_user_id = ?";
        int result = this.jdbcTemplate.queryForObject(query, Integer.class ,targetUserId, userId);

        return result == 0 ? false : true;
    }
}

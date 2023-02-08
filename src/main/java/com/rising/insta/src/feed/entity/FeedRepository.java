package com.rising.insta.src.feed.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.feed.dto.GetReadFeedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FeedRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Feed save(Feed feed) throws BaseException {
        String query = "insert into Feed (user_id, content," +
                "music, pos, image, count) VALUES (?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[]{feed.getUserId(), feed.getContent(), feed.getMusic(), feed.getPos(), feed.getImage(), feed.getCount()};
        try {
            int result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        feed.createData();
        feed.updateData();

        String query1 = "select * from Feed where user_id = ? order by createdAt DESC limit 1";
        int param = feed.getUserId();

        Feed feedEntity = null;
        try {
            feedEntity = this.jdbcTemplate.queryForObject(query1, (rs, count) -> new Feed(
                            rs.getInt("feed_id"),
                            rs.getInt("user_id"),
                            rs.getString("content"),
                            rs.getInt("status"),
                            rs.getString("createdAt"),
                            rs.getString("updatedAt"),
                            rs.getInt("comment_flag"),
                            rs.getInt("like_flag"),
                            rs.getString("music"),
                            rs.getString("pos"),
                            rs.getString("image"),
                            rs.getInt("count")
                    ),
                    param);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return feedEntity;
    }

    /**
     * 1. 전부 조회이기 때문에 query 를 이용하여 조회
     * 2. 대신 해당 유저가 만든 피드여야 하고 현재 삭제가 안된 피드들만 출력
     * */
    public List<GetReadFeedDto> readAll(int userId) throws BaseException {
        String query = "select * from Feed where user_id = ? AND 'status' != 2";
        List<GetReadFeedDto> list = null;
        try {
            list = this.jdbcTemplate.query(query, (rs, count) -> new GetReadFeedDto(
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getString("content"),
                    rs.getInt("status"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("comment_flag"),
                    rs.getInt("like_flag"),
                    rs.getString("music"),
                    rs.getString("pos"),
                    rs.getString("image"),
                    rs.getInt("count")
            ), userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return list;
    }

    public List<Integer> readFeedAllByUserId(int userId) throws BaseException {
        String query = "SELECT feed_id FROM Feed WHERE user_id = ? AND status != 2";
        List<Integer> list = null;
        try {
            list = this.jdbcTemplate.query(query, (rs, count) -> rs.getInt("feed_id"), userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return list;
    }

    public List<GetReadFeedDto> readAll() throws BaseException {
        String query = "select * from Feed where status != 2 ORDER BY rand()";
        List<GetReadFeedDto> list = null;
        try {
            list = this.jdbcTemplate.query(query, (rs, count) -> new GetReadFeedDto(
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getString("content"),
                    rs.getInt("status"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("comment_flag"),
                    rs.getInt("like_flag"),
                    rs.getString("music"),
                    rs.getString("pos"),
                    rs.getString("image"),
                    rs.getInt("count")
            ));
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return list;
    }

    public Feed readFeedById(Integer feedId) throws BaseException {
        String query = "SELECT * FROM Feed WHERE feed_id = ? AND status != 2";
        Feed feed = null;
        try {
            feed = this.jdbcTemplate.queryForObject(query, (rs, count) -> new Feed(
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getString("content"),
                    rs.getInt("status"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getShort("comment_flag"),
                    rs.getInt("like_flag"),
                    rs.getString("music"),
                    rs.getString("pos"),
                    rs.getString("image"),
                    rs.getInt("count")
            ), feedId);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return feed;
    }

    public int updateById(int feedId, String content) throws BaseException {
        String query = "UPDATE Feed SET content = ?, updatedAt = now() WHERE feed_id = ? AND status != 2";
        Object[] params = new Object[]{content, feedId};

        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        return result;
    }

    public int deleteById(Integer feedId) throws BaseException {
        String query = "UPDATE Feed SET status = 2 WHERE feed_id = ?";
        Object[] params = new Object[]{feedId};
        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        return result;
    }

    public int updateLikeStatusByFlag(Integer feedId, Integer flag) throws BaseException {
        String query = "UPDATE Feed SET like_flag = ? WHERE feed_id = ?";
        Object[] params = new Object[]{flag, feedId};

        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    public int updateCommentStatusByFlag(Integer feedId, Integer flag) throws BaseException {
        String query = "UPDATE Feed SET comment_flag = ? WHERE feed_id = ?";
        Object[] params = new Object[]{flag, feedId};
        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }
}

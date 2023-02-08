package com.rising.insta.src.feed.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CommentLikeRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int save(int userId, Integer commentId, String content) throws BaseException {
        String query = "INSERT INTO FeedCommentLike (user_id, comment_id, content) VALUES (?, ?, ?)";
        Object[] params = new Object[] {userId, commentId, commentId};
        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        return result;
    }

    public int delete(int userId, Integer commentId) throws BaseException {
        String query = "DELETE FROM FeedCommentLike WHERE user_id = ? AND comment_id = ?";
        Object[] params = new Object[]{userId, commentId};

        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    public CommentLikeEntity readCommentLikeById(int id) throws BaseException {
        String query = "SELECT * FROM FeedCommentLike WHERE like_id = ?";
        CommentLikeEntity entity = null;
        try {
            entity = this.jdbcTemplate.queryForObject(query, (rs, count) -> new CommentLikeEntity(
                    rs.getInt("like_id"),
                    rs.getInt("user_Id"),
                    rs.getInt("comment_id"),
                    rs.getString("content"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("status")
            ), id);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return entity;
    }
}

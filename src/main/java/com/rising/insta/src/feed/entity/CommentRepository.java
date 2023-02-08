package com.rising.insta.src.feed.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int save(int feedId, int userId, int parentComtId, String content) throws BaseException {
        String query = "INSERT INTO FeedComment (feed_id, user_id, parent_comment_id, content) VALUES (?, ?, ?, ?)";
        Object[] params = new Object[]{feedId, userId, parentComtId, content};
        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    public Comment readOne(int feedId, int userId) throws BaseException {
        String query = "SELECT * FROM FeedComment WHERE feed_id = ? AND user_id = ? ORDER BY comment_id DESC LIMIT 1";
        Comment commentEntity = null;
        try {
            commentEntity = this.jdbcTemplate.queryForObject(query, (rs, count) -> new Comment(
                    rs.getInt("comment_id"),
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getInt("parent_comment_id"),
                    rs.getString("content"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("status")
            ), feedId, userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return commentEntity;
    }

    public Comment readOne(int commentId) throws BaseException {
        String query = "SELECT * FROM FeedComment WHERE comment_id = ?";
        Comment commentEntity = null;
        try {
            commentEntity = this.jdbcTemplate.queryForObject(query, (rs, count) -> new Comment(
                    rs.getInt("comment_id"),
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getInt("parent_comment_id"),
                    rs.getString("content"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("status")
            ), commentId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return commentEntity;
    }

    public int deleteCommentById(Integer commentId) throws BaseException {
        String query = "DELETE FROM FeedComment WHERE comment_id = ?";
        Object[] params = new Object[]{commentId};

        int result;
        try {
            result = this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    public List<Comment> readAllComment(Integer feedId) throws BaseException {
        String query = "SELECT * FROM FeedComment WHERE feed_id = ? AND parent_comment_id = 0";
        List<Comment> commentList = null;
        try {
            commentList = this.jdbcTemplate.query(query, (rs, count) -> new Comment(
                    rs.getInt("comment_id"),
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getInt("parent_comment_id"),
                    rs.getString("content"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("status")
            ), feedId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        return commentList;
    }

    public void deleteAllCommentById(Integer commentId) throws BaseException {
        String query = "DELETE FROM FeedComment WHERE parent_comment_id = ?";
        Object[] params = new Object[]{commentId};
        try {
            this.jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DELETE_FAILED_REPLY);
        }
    }

    public List<Comment> readAllComment(Integer feedId, Integer commentId) throws BaseException {
        String query = "SELECT * FROM FeedComment WHERE feed_id = ? AND parent_comment_id = ?";
        List<Comment> commentList = null;
        try {
            commentList = this.jdbcTemplate.query(query, (rs, count) -> new Comment(
                    rs.getInt("comment_id"),
                    rs.getInt("feed_id"),
                    rs.getInt("user_id"),
                    rs.getInt("parent_comment_id"),
                    rs.getString("content"),
                    rs.getString("createdAt"),
                    rs.getString("updatedAt"),
                    rs.getInt("status")
            ), feedId, commentId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        System.out.println(commentList);
        return commentList;
    }

    public int countCommentByFeedId(int feedId) throws BaseException {
        String query = "SELECT count(*) FROM FeedComment WHERE feed_id = ? AND status = 0";
        int result;
        try {
            result = this.jdbcTemplate.queryForObject(query, Integer.class, feedId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    public int countReplyByCommentId(int commentId) throws BaseException {
        String query = "SELECT count(*) FROM FeedComment WHERE parent_comment_id = ? AND status = 0";
        int result;
        try {
            result = this.jdbcTemplate.queryForObject(query, Integer.class, commentId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }
}

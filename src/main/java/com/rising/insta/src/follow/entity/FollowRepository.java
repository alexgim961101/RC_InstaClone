package com.rising.insta.src.follow.entity;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.follow.dto.FollowUser;
import com.rising.insta.src.follow.dto.FollowingUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FollowRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 1. 이미 follow 중인지 check (구동 중이면 예외 throw 41013 예외 가능성)
     * 2. follow 안하고 있다면 Follow 테이블에 Follow 정보 저장
     * 3. int 값 반환 (1 : 성공 , 0 : 팔로우 실패)
     * */
    public int saveFollow(int fromUserId, int toUserId) throws BaseException {
        String query1 = "select count(*) from Follow where from_user_id = ? AND to_user_id = ?";
        if(this.jdbcTemplate.queryForObject(query1, Integer.class, fromUserId, toUserId) != 0) throw new BaseException(BaseResponseStatus.FOLLOW_EXIST);


        String query2 = "insert into Follow (from_user_id, to_user_id) VALUES (?, ?)";
        Object[] params = new Object[]{fromUserId, toUserId};

        int result = this.jdbcTemplate.update(query2, params);

        return result;
    }

    /**
     * 1. 이미 follow 중인지 check (구동 중이면 예외 throw 41014 예외 가능성)
     * 2. follow 하고 있다면 Follow 테이블에 Follow 정보 삭제 (이 과정에서 DB 에외 4000 가능성)
     * 3. int 값 반환 (1 : 성공 , 0 : 팔로우 취소 실패)
     * */
    public int deleteFollow(int fromUserId, int toUserId) throws BaseException {
        String query1 = "select count(*) from Follow where from_user_id = ? AND to_user_id = ?";
        if(this.jdbcTemplate.queryForObject(query1, Integer.class, fromUserId, toUserId) != 1) throw new BaseException(BaseResponseStatus.FOLLOW_NOT_EXIST);

        String query2 = "delete from Follow where from_user_id = ? AND to_user_id = ?";
        Object[] params = new Object[]{fromUserId, toUserId};

        int result;
        try {
            result = this.jdbcTemplate.update(query2, params);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    /**
     * 4000예외 가능성
     * */
    public List<FollowUser> readAllFollowerList(int userId) throws BaseException {
        String query = "SELECT f.from_user_id, u.name, u.content, u.image_url FROM Follow f INNER JOIN User u ON f.from_user_id = u.user_id WHERE f.to_user_id = ? AND u.status = 0";
        List<FollowUser> userList = null;
        try {
            userList = this.jdbcTemplate.query(query, (rs, count) -> new FollowUser(
                            rs.getInt("f.from_user_id"),
                            rs.getString("u.name"),
                            rs.getString("u.content"),
                            rs.getString("u.image_url")
                    ),
                    userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return userList;
    }

    /**
     * 4000 예외 가능성
     * */
    public List<FollowingUser> readAllFollowingList(int userId) throws BaseException {
        String query = "SELECT f.to_user_id, u.name, u.content, u.image_url FROM Follow f INNER JOIN User u ON f.to_user_id = u.user_id WHERE f.from_user_id = ? AND u.status = 0";
        List<FollowingUser> userList = null;
        try {
            userList = this.jdbcTemplate.query(query, (rs, count) -> new FollowingUser(
                            rs.getInt("f.to_user_id"),
                            rs.getString("u.name"),
                            rs.getString("u.content"),
                            rs.getString("u.image_url")
                    ),
                    userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return userList;
    }
}

package com.rising.insta.src.user;


import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.config.Constant;
import com.rising.insta.src.user.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    // 회원가입
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User "
        		+ "(login_id, type, name, password"
        		+ (postUserReq.getEmail() 	 != null ? ", email " : "")
        		+ (postUserReq.getPhone() 	 != null ? ", phone " : "")
        		+ (postUserReq.getAge() 	 != null ? ", age " : "")
        		+ ") VALUES (?,?,?,?"
        		+ (postUserReq.getEmail() 	 != null ? ",?" : "") 
        		+ (postUserReq.getPhone() 	 != null ? ",?" : "") 
        		+ (postUserReq.getAge() 	 != null ? ",?" : "") 
        		+ ")"; // 실행될 동적 쿼리문
        List<Object> createUserParams = new ArrayList<>(); 
        createUserParams.add(postUserReq.getLoginId());
        createUserParams.add(postUserReq.getType());
        createUserParams.add(postUserReq.getName());
        createUserParams.add(postUserReq.getPassword());
        
        if (postUserReq.getEmail() != null) {
        	createUserParams.add(postUserReq.getEmail());
        }
        if (postUserReq.getPhone() != null) {
        	createUserParams.add(postUserReq.getPhone());
        }
        if (postUserReq.getAge() != null) {
        	createUserParams.add(postUserReq.getAge());
        }
        // 동적 쿼리의 ?부분에 주입될 값
        
        this.jdbcTemplate.update(createUserQuery, createUserParams.toArray());

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userId번호를 반환
    }

    // LoginId 확인
    public int checkLoginId(String loginId) {
        String checkLoginIdQuery = "select exists(select login_id from User where login_id = ? and status != 1)";
        String checkLoginIdParams = loginId; // 해당(확인할) 값
        return this.jdbcTemplate.queryForObject(checkLoginIdQuery,
                int.class,
                checkLoginIdParams); // -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 회원정보 변경
    public int updateUser(PatchUserReq patchUserReq) {
    	
    	String update = "update User set ";
        String set = "";
        String where = "where user_id = ?  and status != 1";
    	
        List<Object> list = new ArrayList<>();

        if (patchUserReq.getPassword() != null) {
        	set += "password = ?, ";
        	list.add(patchUserReq.getPassword());
        }
        if (patchUserReq.getName() != null) {
        	set += "name = ?, ";
        	list.add(patchUserReq.getName());
        }
        if (patchUserReq.getAge() != null) {
        	set += "age = ?, ";
        	list.add(patchUserReq.getAge());
        }
        if (patchUserReq.getContent() != null) {
        	set += "content = ?, ";
        	list.add(patchUserReq.getContent());
        }
        if (patchUserReq.getPhone() != null) {
        	set += "phone = ?, ";
        	list.add(patchUserReq.getPhone());
        }
        if (patchUserReq.getEmail() != null) {
        	set += "email = ?, ";
        	list.add(patchUserReq.getEmail());
        }
//        if (patchUserReq.getType() != null) {
//        	set += "type = ?, ";
//        	list.add(patchUserReq.getType());
//        }
        if (patchUserReq.getImageUrl() != null) {
        	set += "image_url = ?, ";
        	list.add(patchUserReq.getImageUrl());
        }
        
        // 마지막 column , 제거
        set = set.substring(0, set.lastIndexOf(",")) + " ";
        
        list.add(patchUserReq.getUserId());
        
        String updateUserQuery = update + set + where; // 해당 userId를 만족하는 User를 해당 User 정보로 업데이트 한다
        Object[] updateUserParams = list.toArray();

        return this.jdbcTemplate.update(updateUserQuery, updateUserParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0) 
    }


    // 로그인: 해당 loginId에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select user_id, login_id, password, type from User where login_id = ? and status != 1"; // 해당 loginId 을 만족하는 User의 정보들을 조회
        String getPwdParams = postLoginReq.getLoginId(); 

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
            		rs.getInt("user_id"),
                    rs.getString("login_id"),
                    rs.getString("password"),
                    rs.getInt("type")
                ), 
                getPwdParams
        ); 
    }

    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User where status != 1";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                		rs.getInt("user_id"),
                        rs.getString("login_id"),
                        rs.getInt("type"),
//                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("content"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()) 
        ); 
    }

//    // 해당 nickname을 갖는 유저들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) {
//        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
//        String getUsersByNicknameParams = nickname;
//        return this.jdbcTemplate.query(getUsersByNicknameQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
    // userId 확인
    public int checkUserId(int userId) {
        String checkUserIdQuery = "select exists(select user_id from User where user_id = ? and status != 1)";
        int checkUserIdParams = userId; // 해당(확인할) 값
        return this.jdbcTemplate.queryForObject(checkUserIdQuery,
                int.class,
                checkUserIdParams); // -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    
    // 해당 userId를 갖는 유저조회
    public GetUserRes getUser(int userId) {
        String getUserQuery = "select * from User where user_id = ? and status != 1"; // 해당 userId를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("user_id"),
                        rs.getString("login_id"),
                        rs.getInt("type"),
//                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("content"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getString("status"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ), 
                getUserParams); 
    }

    // 해당 userId의 추천 유저 목록 조회
	public List<GetRecommendUserRes> getRecommendUsers(int userId, int limitCount) {
		String getRecommendUsersQuery = "SELECT * FROM User "
				+ "WHERE user_id NOT IN ( (SELECT to_user_id FROM Follow WHERE from_user_id = ?), ? ) "
				+ "ORDER BY rand() limit ?";
		Object[] getRecommendUserParams = new Object[] { userId, userId, limitCount };
		return this.jdbcTemplate.query(getRecommendUsersQuery,
              (rs, rowNum) -> new GetRecommendUserRes(
                      rs.getInt("user_id"),
                      rs.getString("name"),
                      rs.getString("image_url")
              ),
              getRecommendUserParams);
	}

	// 해당 userId가 adminId인지 확인 - type으로 구분
	public int checkAdminId(int userId) {
		String checkAdminIdQuery = "select exists(select user_id from User where user_id = ? and type = 3 and status != 1)";
        int checkAdminIdParams = userId; // 해당(확인할) 값
        return this.jdbcTemplate.queryForObject(checkAdminIdQuery,
                int.class,
                checkAdminIdParams);
	}

	public int deleteUser(int userId) {
		String deleteUserQuery = "UPDATE User SET status = 1 WHERE user_id = ?";
		int deleteUserParams = userId;
		return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
	}

    // 로또 - 댓글 기능에서 유저 이름이 필요해서 함수 만듬
    public String readNameById(int userId) throws BaseException {
        String query = "SELECT name FROM User WHERE user_id = ?";
        String result;
        try {
            result = this.jdbcTemplate.queryForObject(query, String.class, userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

    // 로또 - 댓글 기능에서 유저 프로필 이미지 url이 필요해서 만듬
    public String readImageById(int userId) throws BaseException {
        String query = "SELECT image_url FROM User WHERE user_id = ?";
        String result;
        try {
            result = this.jdbcTemplate.queryForObject(query, String.class, userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        return result;
    }

	public int checkPhone(String phone) {
		String checkPhoneQuery = "select exists(select login_id from User where phone = ? and status != 1)";
        String checkPhoneParams = phone; // 해당(확인할) 값
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkPhoneParams); // -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
	}


}

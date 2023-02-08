package com.rising.insta.src.user.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class User {
	private int userId;
	private String loginId;
	private int type;
	private String password;
	private String name;
	private int age;
	private String content;
	private String phone;
	private String email;
	private String imageUrl;
	private String status;
	private LocalDateTime createdAt;

	public User(int userId, String loginId, String password, int type) {
		this.userId = userId;
		this.loginId = loginId;
		this.password = password;
		this.type = type;
	}
}

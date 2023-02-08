package com.rising.insta.src.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
	private int userId;
	private String loginId;
	private int type;
//	private String password;
	private String name;
	private int age;
	private String content;
	private String phone;
	private String email;
	private String imageUrl;
	private String status;
	private LocalDateTime createdAt;
}

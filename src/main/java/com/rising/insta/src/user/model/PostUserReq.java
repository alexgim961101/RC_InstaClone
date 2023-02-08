package com.rising.insta.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserReq {
	private String loginId;
	private int type;
	private String name;
	private String password;
	private Integer age;
	private String phone;
	private String email;

	public PostUserReq(String loginId, int type, String name, String password, String email) {
		super();
		this.loginId = loginId;
		this.type = type;
		this.name = name;
		this.password = password;
		this.email = email;
	}

}

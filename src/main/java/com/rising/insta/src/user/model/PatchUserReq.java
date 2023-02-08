package com.rising.insta.src.user.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PatchUserReq {
	private int userId;
//	private int loginId;
	private String password;
	private String name;
	private Integer age;
	private String content;
	private String phone;
	private String email;
//	private String type;
	private String imageUrl;

	public boolean checkUpdateInfo() {
		if (password != null) {
			return true;
		}

		if (name != null) {
			return true;
		}

		if (age != null) {
			return true;
		}

		if (content != null) {
			return true;
		}

		if (phone != null) {
			return true;
		}

		if (email != null) {
			return true;
		}

//		if (type != null) {
//			return true;
//		}

		if (imageUrl != null) {
			return true;
		}

		return false;
	}
}

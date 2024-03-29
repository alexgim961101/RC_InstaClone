package com.rising.insta.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostSocialLoginRes {
	private Long loginId;
	private String email;
    private String userName;
}

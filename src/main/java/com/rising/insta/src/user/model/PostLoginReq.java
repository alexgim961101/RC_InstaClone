package com.rising.insta.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostLoginReq {
	private String loginId;
	private String password;
}

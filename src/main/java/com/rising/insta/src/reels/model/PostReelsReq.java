package com.rising.insta.src.reels.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReelsReq {
	private int userId;

	private String url;
	private String content;
}

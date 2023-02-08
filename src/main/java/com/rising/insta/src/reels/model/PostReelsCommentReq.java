package com.rising.insta.src.reels.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReelsCommentReq {
	private int userId;
	private int reelsId;
	private Integer parentCommentId;

	private String content;
}

package com.rising.insta.src.reels.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetReelsCommentRes {
	private int commentId;
	private Integer parentCommentId;
	private int reelsId;
	private int userId;
	
	private int level;

	private String content;
	
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

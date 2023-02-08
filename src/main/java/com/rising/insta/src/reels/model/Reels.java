package com.rising.insta.src.reels.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter 
@Setter 
@AllArgsConstructor 
public class Reels {
	private int reelsId;
	private int userId;

	private String url;
	private String content;
	
	private String status;
	private LocalDateTime createdAt;
}

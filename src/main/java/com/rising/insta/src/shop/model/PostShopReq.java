package com.rising.insta.src.shop.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostShopReq {
	private int userId;
	
	private String name;
	private String iconUrl;
	private String content;
}

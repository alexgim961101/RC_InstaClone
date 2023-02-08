package com.rising.insta.src.shop.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetShopRes {
	private int shopId;

	private String name;
	private String iconUrl;
	private String content;
	
	private String status;
	private LocalDateTime createdAt;
}

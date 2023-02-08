package com.rising.insta.src.item.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter 
@Setter 
@AllArgsConstructor 
public class Item {
	private int itemId;
	private int userId;
	private int shopId;

	private String name;
	private String content;
	private Integer price;
	private String url;
	
	private String status;
	private LocalDateTime createdAt;
}

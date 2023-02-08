package com.rising.insta.src.item.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchItemReq {
	private int itemId;
	private int userId;
	private int shopId;

	private String name;
	private String content;
	private Integer price;
	private String url;
	
	public boolean checkUpdateInfo() {
		if (name != null) {
			return true;
		}
		
		if (content != null) {
			return true;
		}
		
		if (price != null) {
			return true;
		}
		
		if (url != null) {
			return true;
		}
		
		return false;
	}
}

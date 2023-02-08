package com.rising.insta.src.shop.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchShopReq {
	private int shopId;
	private int userId;

	private String name;
	private String iconUrl;
	private String content;
	
	public boolean checkUpdateInfo() {
		if (name != null) {
			return true;
		}
		
		if (iconUrl != null) {
			return true;
		}
		
		if (content != null) {
			return true;
		}
		
		return false;
	}
}

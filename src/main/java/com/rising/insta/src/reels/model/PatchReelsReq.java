package com.rising.insta.src.reels.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchReelsReq {
	private int reelsId;
	private int userId;

	private String url;
	private String content;
	
	public boolean checkUpdateInfo() {
		if (url != null) {
			return true;
		}
		
		if (content != null) {
			return true;
		}
		
		return false;
	}
}

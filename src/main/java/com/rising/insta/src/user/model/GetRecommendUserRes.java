package com.rising.insta.src.user.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRecommendUserRes {
	private int userId;
	private String name;
	private String imageUrl;
}

package com.rising.insta.config;

// 프로젝트에서 공통적으로 사용하는 상수들
public class Constant {
    // 예시 public static final String IP_ADDRESS = "127.0.0.1";
	
	// 대응시켜 매핑시켜 존재여부 쿼리 요청 - 존재하면 1 없으면 0 
	public enum ExistQueryResult {
		NOT_EXIST, EXIST, 
	}
	
	// 기본 쿼리 - 실패 0, 성공 1
	public enum BasicQueryResult {
		FAIL, SUCESS
	}
	
	// 추천 유전 기본 추천 수
	public static final int RECOMMENT_USER_DEFAULT_LIMIT = 3;
	
	// 기본 랜덤 릴스 조회 수
	public static final int RANDOM_REELS_DEFAULT_LIMIT = 1; 
	
	// 기본 랜덤 아이템 조회 수
	public static final int RANDOM_ITEM_DEFAULT_LIMIT = 6;
}


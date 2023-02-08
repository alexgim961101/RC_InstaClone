package com.rising.insta.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    AUTH_PHONE_INVALID_SEND_MESSAGE(false, 2004, "문자 인증 전송에 실패하였습니다."),

    // user
    USERS_EMPTY_USER_ID(false, 22001, "유저 아이디 값을 확인해주세요."),
    USER_NOT_EXIST_USER_ID(false, 22002, "유저 아이디가 존재하지 않습니다."),
    // reels
    REELS_NOT_EXIST_REELS_ID(false, 22003, "릴스 아이디가 존재하지 않습니다."),
    // item
    ITEM_NOT_EXIST_ITEM_ID(false, 22004, "아이템 아이디가 존재하지 않습니다."),
    // shop
    SHOP_NOT_EXIST_SHOP_ID(false, 22005, "가게 아이디가 존재하지 않습니다."),
    

    // [POST] /user
    POST_USER_EMPTY_EMAIL(false, 22010, "이메일을 입력해주세요."),
    POST_USER_INVALID_EMAIL(false, 22011, "이메일 형식을 확인해주세요."),
    POST_USER_EMPTY_PHONE(false, 22012, "핸드폰 번호를 입력해주세요."),
    POST_USER_INVALID_PHONE(false, 22013, "핸드폰 번호 형식을 확인해주세요."),
    POST_USER_EXISTS_LOGIN_ID(false, 22014,"중복된 로그인 아이디입니다."),
    POST_USER_EMPTY_LOING_ID(false, 22015, "로그인 아이디를 입력해주세요."),
    POST_USER_EMPTY_PASSWORD(false, 22016, "비밀번호를 입력해주세요."),
    
    // [PATCH] /user
    PATCH_USER_UPDATE_INFO(false, 22020, "수정할 유저 정보가 없습니다."),

    FEED_IMAGE_EMPTY(false, 2018, "이미지를 넣어주세요"),
    FEED_IMAGE_OVERFLOW(false, 2019, "이미지가 10개를 초과하였습니다"),
    INVALID_FILE_CONTENT_TYPE(false, 2020, "유효하지 않은 확장자명입니다"),
    MISMATCH_IMAGE_FILE(false, 2021, "jpeg, png 파일이 아닙니다."),

    INVALID_ACCESS(false, 21022, "잘못된 접근입니다"),
    WRITE_COMMENT(false, 21023, "댓글을 입력해주세요"),
    WRITE_CONTENT(false, 21024, "글을 입력해주세요"),
    NOT_UPDATE_AUTH(false, 21025, "수정 권한이 없습니다"),
    EQUAL_PRECONTENT(false, 21026, "이전 글과 동일합니다"),
    INVALID_FLAG(false, 21027, "잘못된 상태값입니다"),
    EQUAL_FLAG(false, 21028, "기존 상태와 동일합니다"),
    
    // [POST] /reels
    POST_REELS_EMPTY_REELS_URL(false, 22030, "동영상 URL을 입력해주세요."),
    POST_REELS_COMMENT_EMPTY_CONTENT(false, 22031, "릴스 댓글 내용을 입력해주세요."),
    POST_REELS_COMMENT_SELF_USER_ID(false, 22032, "본인 릴스에 댓글을 입력할 수 없습니다."),
    
    // [PATCH] /reels
    PATCH_REELS_UPDATE_INFO(false, 22040, "수정할 릴스 정보가 없습니다."),
    
    // [POST] /item
    POST_ITEM_EMPTY_NAME(false, 22050, "이름을 입력해주세요."),
    POST_ITEM_EMPTY_PRICE(false, 22051, "가격을 입력해주세요."),
    POST_ITEM_EMPTY_IMAGE_URL(false, 22052, "이미지 URL을 입력해주세요."),
    
    // [POST] /shop
    POST_SHOP_EMPTY_NAME(false, 22053, "이름을 입력해주세요."),
    POST_SHOP_EMPTY_CONTENT(false, 22054, "소개를 입력해주세요."),
    
    // [PATCH] /item
    PATCH_ITEM_UPDATE_INFO(false, 22060, "수정할 아이템 정보가 없습니다."),

    // [PATCH] /shop
    PATCH_SHOP_UPDATE_INFO(false, 22061, "수정할 가게 정보가 없습니다."),
    
    // [POST] /auth/phone
    AUTH_PHONE_EXIST_PHONE(false, 22070, "이미 가입된 핸드폰 번호입니다."),
    AUTH_PHONE_INVALID_PHONE_AUTH(false, 22071, "문자 인증에 실패하였습니다."),
    
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /user
    DUPLICATED_LOGIN_ID(false, 32001, "중복된 로그인 아이디입니다."),
    FAILED_TO_LOGIN(false, 32002, "없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    // [PATCH] /user
    MODIFY_USER_FAIL(false, 42000, "유저 정보 수정 실패하였습니다."),
    // [PATCH] /reels
    MODIFY_REELS_FAIL(false, 42001, "릴스 정보 수정 실패하였습니다."),
    // [PATCH] /item
    MODIFY_ITEM_FAIL(false, 42002, "아이템 정보 수정 실패하였습니다."),
    // [PATCH] /shop
    MODIFY_SHOP_FAIL(false, 42003, "가게 정보 수정 실패하였습니다."),
    
    // [DELETE] /user
    DELETE_USER_FAIL(false, 42010, "유저 정보 삭제 실패하였습니다."),
    // [DELETE] /reels
    DELETE_REELS_FAIL(false, 42011, "릴스 정보 삭제 실패하였습니다."),
    // [DELETE] /item
    DELETE_ITEM_FAIL(false, 42012, "아이템 정보 삭제 실패하였습니다."),
    // [DELETE] /shop
    DELETE_SHOP_FAIL(false, 42013, "가게 정보 삭제 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    FOLLOW_EXIST(false, 41013, "이미 팔로우중입니다"),
    FOLLOW_NOT_EXIST(false, 41014, "팔로우 상대가 아닙니다"),
    LIKE_EXIST(false, 41015, "이미 좋아요를 눌렀습니다"),
    LIKE_NOT_EXIST(false, 41016, "좋아요를 누른적이 없습니다"),
    NOT_SAVE_COMMENT(false, 41017, "DB에 댓글 저장을 실패했습니다"),
    DELETE_FAILED_REPLY(false, 41018, "대댓글 리스트 삭제에 실패했습니다"),


    // 5000 : 필요시 만들어서 쓰세요
    CONVERT_FAILED(false, 51001, "MultipartFile -> File 변환에 실패하여습니다"),
    S3_CONNECT_FAILED(false, 51002, "S3 연결에 실패하였습니다"),
    COMMENT_CAN_NOT_DELETE(false, 51003, "댓글을 삭제할 권한이 없습니다");

    // 6000 : 필요시 만들어서 쓰세요



    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

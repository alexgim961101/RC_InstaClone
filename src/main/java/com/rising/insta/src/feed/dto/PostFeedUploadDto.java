package com.rising.insta.src.feed.dto;

import com.rising.insta.src.feed.entity.Feed;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostFeedUploadDto {
    // 이미지 파일 리스트
    private List<MultipartFile> fileList;
    // 태그된 사람 리스트
    private List<String> tags;
    // 피드 내용
    private String content;
    // 음악
    private String music;
    // 위치
    private String pos;

    public Feed toEntity(int userId, String imgUrl, int count){
        return Feed.builder()
                .userId(userId)
                .content(content)
                .commentFlag(0)
                .likeFlag(0)
                .music(music)
                .pos(pos)
                .image(imgUrl)
                .count(count).build();
    }
}

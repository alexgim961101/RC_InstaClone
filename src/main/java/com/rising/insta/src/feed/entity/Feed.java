package com.rising.insta.src.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Feed {
    private int feedId;
    private int userId;

    private String content;
    private int status;
    private String createdAt;
    private String updatedAt;
    private int commentFlag;
    private int likeFlag;
    private String music;
    private String pos;

    private String image;
    private int count;

    public void createData() {
        this.createdAt = LocalDateTime.now().toString();
    }

    public void updateData() {
        this.updatedAt = LocalDateTime.now().toString();
    }
}

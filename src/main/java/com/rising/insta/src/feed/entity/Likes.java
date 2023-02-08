package com.rising.insta.src.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Likes {
    private int LikeId;
    private int feedId;
    private int userId;
    private int like_status;
    private String createAt;

    public void createTime() {
        this.createAt = LocalDateTime.now().toString();
    }
}

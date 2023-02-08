package com.rising.insta.src.follow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Follow {
    private int id;
    private int toUserId;
    private int fromUserId;
    private String createdAt;

    public void createTime() {
        this.createdAt = LocalDateTime.now().toString();
    }
}

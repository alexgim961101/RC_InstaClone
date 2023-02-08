package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PatchFeedReq {
    private int userId;
    private String preContent;
    private String newContent;
}

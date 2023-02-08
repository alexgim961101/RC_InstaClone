package com.rising.insta.src.dm.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetDMResp {
    private String userImg;
    private String name;
    private String content;
    private String time;
}

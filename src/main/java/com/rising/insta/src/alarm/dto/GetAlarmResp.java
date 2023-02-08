package com.rising.insta.src.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetAlarmResp {
    private int alarmId;
    private int userId;
    private String userImg;
    private String name;
    private String content;
    private String createdAt;
}

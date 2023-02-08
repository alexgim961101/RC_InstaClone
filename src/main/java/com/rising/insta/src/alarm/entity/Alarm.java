package com.rising.insta.src.alarm.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Alarm {
    private int alarmId;
    private int senderId;
    private int receiverId;
    private int type;
    private int contentId;
    private String createdAt;
}

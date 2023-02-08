package com.rising.insta.src.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class PatchManiLikeReq {
    private int userId;
    private int preFlag;
}

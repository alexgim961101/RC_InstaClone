package com.rising.insta.src.feed.dto;

import com.rising.insta.src.feed.entity.Comment;
import com.rising.insta.src.feed.entity.Feed;
import com.rising.insta.src.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetFollowingFeedResp {
    private int feedId;
    private int userId;
    private String name;
    private String userImage;
    private String image;
    private String music;
    private int imageCount;
    private boolean like;             // 내가 좋아요를 누른 게시물인지
    private int likeExist;
    private int likeCount;
    private String content;
    private int commentExist;
    private String time;
    private int commentCount;


}

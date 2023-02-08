package com.rising.insta.src.feed.dto;

import com.rising.insta.src.feed.entity.Comment;
import com.rising.insta.src.feed.entity.Feed;
import com.rising.insta.src.user.model.GetUserRes;
import com.rising.insta.src.user.model.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
public class GetFeedDetailResp {
    private GetUserRes feedWriter;
    private Feed feed;
    private List<GetUserRes> commentWriter;
    private List<GetCommentListResp> comment;
}

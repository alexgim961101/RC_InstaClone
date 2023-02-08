package com.rising.insta.src.alarm;

import com.rising.insta.config.BaseException;
import com.rising.insta.src.alarm.dto.GetAlarmResp;
import com.rising.insta.src.alarm.entity.Alarm;
import com.rising.insta.src.alarm.entity.AlarmRepository;
import com.rising.insta.src.feed.entity.*;
import com.rising.insta.src.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserDao userDao;
    private final FeedRepository feedRepository;
    private final FeedLikesRepository feedLikesRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public List<GetAlarmResp> readAllAlarm(int userId) throws BaseException {
        List<Alarm> alarmEntityList = alarmRepository.readAllAlarmByReceiverId(userId);
        List<GetAlarmResp> getAlarmRespList = new ArrayList<>();

        for(Alarm entity : alarmEntityList){
            String userImg = userDao.readImageById(entity.getSenderId());
            String name = userDao.readNameById(entity.getSenderId());

            int type = entity.getType();
            int id = entity.getContentId();


            String content;
            switch (type) {
                case 0:
                    content = name + "님의 팔로잉 신청";
                    break;
                case 1:
                    Feed feed1 = feedRepository.readFeedById(id);
                    content = name + "님이 "+ feed1.getContent() +" 피드에 좋아요를 눌렀습니다";
                    break;
                case 2:
                    Feed feed2 = feedRepository.readFeedById(id);
                    content = name + "님이"+ feed2.getContent() +" 피드에 댓글을 달았습니다";
                    break;
                case 3:
                    Comment comment1 = commentRepository.readOne(id);
                    content = name + "님이 "+ comment1.getContent() +" 댓글에 대댓글을 달았습니다";
                    break;
                case 4:
                    CommentLikeEntity commentLikeEntity = commentLikeRepository.readCommentLikeById(id);
                    content = name + "님의 "+ commentLikeEntity.getContent() +" 댓글에 좋아요를 눌렀습니다";
                    break;
                case 5:
                    content = name + "님이 스토리에 좋아요를 눌렀습니다";
                    break;
                default:
                    content = name + "님이 릴스에 좋아요를 눌렀습니다";
            }

            GetAlarmResp getAlarmResp = GetAlarmResp.builder()
                    .alarmId(entity.getAlarmId())
                    .userId(entity.getSenderId())
                    .userImg(userImg)
                    .name(name)
                    .content(content)
                    .createdAt(entity.getCreatedAt()).build();

            getAlarmRespList.add(getAlarmResp);
        }
        return getAlarmRespList;
    }
}

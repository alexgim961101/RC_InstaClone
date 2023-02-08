package com.rising.insta.src.feed;

import com.rising.insta.config.BaseException;
import com.rising.insta.config.BaseResponseStatus;
import com.rising.insta.src.alarm.util.SaveAlarm;
import com.rising.insta.src.feed.dto.*;
import com.rising.insta.src.feed.entity.*;
import com.rising.insta.src.follow.dto.FollowingUser;
import com.rising.insta.src.follow.entity.FollowRepository;
import com.rising.insta.src.user.UserDao;
import com.rising.insta.src.user.model.GetUserRes;
import com.rising.insta.src.user.model.User;
import com.rising.insta.utils.s3.S3Component;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final FeedLikesRepository feedLikesRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final UserDao userDao;
    private final CommentLikeRepository commentLikeRepository;
    private final SaveAlarm saveAlarm;
    private final S3Component s3;

    /**
     * 1. S3에 업로드
     * 2. S3에 저장된 경로를 가져온다.
     * 3. 파일의 갯수, 가장 앞의 이미지, 유저 인덱스를 이용해 feed 엔티티 생성
     * 4. 레포지토리 호출
     * */
    @Transactional
    public Feed saveFeed(int idx, PostFeedUploadDto postFeedUploadDto) throws BaseException {
        List<String> urlList = new ArrayList<>();
        // 1. S3에 업로드
        for(MultipartFile file : postFeedUploadDto.getFileList()){
            try {
                // 2. S3에 업로드함과 동시에 경로 받아오기
                urlList.add(s3.saveFile(file));
            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.S3_CONNECT_FAILED);
            }
        }

        // 3. 파일의 갯수, 가장 앞의 이미지, 유저 인덱스를 이용해 feed 엔티티 생성
        int count = urlList.size();
        String imgUrl = urlList.get(0);
        Feed feed = postFeedUploadDto.toEntity(idx, imgUrl, count);

        // 4. 레포지토리 호출
        Feed feedEntity = feedRepository.save(feed);
        return feedEntity;
    }

    /**
     * 1. DB에서 해당 유저에 해당하는 피드들을 가져오면 끝
     * */
    @Transactional
    public List<GetReadFeedDto> AllFeed(int userId) throws BaseException {
        return feedRepository.readAll(userId);
    }

    @Transactional
    public PostLikeResp createLike(int idx, int feedId) throws BaseException {
        PostLikeResp postLikeResp = feedLikesRepository.saveLike(idx, feedId);
        return postLikeResp;
    }

    @Transactional
    public PatchLikeResp deleteLike(int idx, int feedId) throws BaseException {
        return feedLikesRepository.patchLike(idx, feedId);
    }

    @Transactional
    public List<GetLikeUserList> userList(int idx, int feedId) throws BaseException {
        List<GetLikeUserList> list = new ArrayList<>();
        List<LikeUser> userInfList = feedLikesRepository.getUserLikeList(feedId);
        for(LikeUser u : userInfList) {
            boolean followFlag = false;
            boolean mySelfFlag = false;
            if(feedLikesRepository.checkFollow(idx, u.getUserId())){
                followFlag = true;
            }
            if(u.getUserId() == idx) mySelfFlag = true;
            GetLikeUserList getLikeUserList = new GetLikeUserList(u.getUserId(), u.getNickname(), u.getImage(), followFlag, mySelfFlag ,u.getTime());
            list.add(getLikeUserList);
        }
        return list;
    }

    /********************************************* 피드 댓글 **************************************************/
    @Transactional
    public PostComtRes saveComment(int feedId, int userId ,int parentCode , String content) throws BaseException {

        int resultSave = commentRepository.save(feedId, userId, parentCode, content);
        if(resultSave != 1) throw new BaseException(BaseResponseStatus.NOT_SAVE_COMMENT);

        Comment commentEntity = commentRepository.readOne(feedId, userId);
        String name = userDao.readNameById(userId);
        String image = userDao.readImageById(userId);
        List<Integer> feedList = feedRepository.readFeedAllByUserId(userId);
        boolean checkMyFeed = false;
        if(feedList == null) checkMyFeed = false;
        else {
            for (int x : feedList) {
                if(x == feedId) {
                    checkMyFeed = true;
                    break;
                }
            }
        }

        PostComtRes postComtRes = PostComtRes.builder()
                .commentId(commentEntity.getCommentId())
                .userId(commentEntity.getUserId())
                .name(name)
                .image(image)
                .content(content)
                .createdAt(commentEntity.getCreatedAt())
                .myFeed(checkMyFeed)
                .myComment(true).build();
        return postComtRes;
    }

    // 부모가 삭제되는 경우와 자식이 삭제되는 경우로 나눠야 함
    @Transactional
    public int deleteComment(int userId, Integer feedId, Integer commentId) throws BaseException {
        Comment commentEntity = commentRepository.readOne(commentId);

        if(commentEntity.getUserId() != userId) throw new BaseException(BaseResponseStatus.COMMENT_CAN_NOT_DELETE);

        int result;
        if(commentEntity.getCommentId() == 0) {
            result = commentRepository.deleteCommentById(commentId);
            commentRepository.deleteAllCommentById(commentId);
        } else {
            result = commentRepository.deleteCommentById(commentId);
        }
        return result;
    }

    @Transactional
    public List<GetCommentListResp> getCommentList(int userId, Integer feedId) throws BaseException {

        // 부모 댓글 리스트
        List<Comment> commentEntityList = commentRepository.readAllComment(feedId);

        List<GetCommentListResp> list = new ArrayList<>();

        for(Comment c : commentEntityList) {
            String name = userDao.readNameById(userId);
            String image = userDao.readImageById(userId);
            boolean myFeed = false;
            boolean myComment = false;
            int replyCount = commentRepository.countReplyByCommentId(c.getCommentId());

            List<Integer> integerList = feedRepository.readFeedAllByUserId(userId);
            for(int x : integerList) {
                if(x == feedId) {
                    myFeed = true;
                    break;
                }
            }

            if(c.getUserId() == userId) myComment = true;

            GetCommentListResp getCommentListResp = GetCommentListResp.builder()
                    .parentCommentId(c.getParentCommentId())
                    .commentId(c.getCommentId())
                    .feedId(c.getFeedId())
                    .userId(c.getUserId())
                    .name(name)
                    .image(image)
                    .content(c.getContent())
                    .time(c.getCreatedAt())
                    .myFeed(myFeed)
                    .myComment(myComment)
                    .replyCount(replyCount).build();
            list.add(getCommentListResp);
        }

        return list;
    }

    @Transactional
    public List<GetReplyListResp> getReplyList(int userId, Integer feedId, Integer commentId) throws BaseException {
        List<Comment> commentEntityList = commentRepository.readAllComment(feedId, commentId);

        List<GetReplyListResp> list = new ArrayList<>();

        for(Comment c : commentEntityList) {
            String name = userDao.readNameById(userId);
            String image = userDao.readImageById(userId);
            boolean myFeed = false;
            boolean myComment = false;

            List<Integer> integerList = feedRepository.readFeedAllByUserId(userId);
            for(int x : integerList) {
                if(x == feedId) {
                    myFeed = true;
                    break;
                }
            }

            if(c.getUserId() == userId) myComment = true;

            GetReplyListResp getReplyListResp = GetReplyListResp.builder()
                    .parentCommentId(c.getParentCommentId())
                    .commentId(c.getCommentId())
                    .feedId(c.getFeedId())
                    .userId(c.getUserId())
                    .name(name)
                    .image(image)
                    .content(c.getContent())
                    .time(c.getCreatedAt())
                    .myFeed(myFeed)
                    .myComment(myComment).build();
            list.add(getReplyListResp);
        }


        return list;

    }

    // 정렬까지 추가하면 완벽 (완료)
    @Transactional
    public List<GetFollowingFeedResp> getFollowingFeedList(int userId) throws BaseException {
        // 내 팔로잉 목록 확인
        List<FollowingUser> followingList = followRepository.readAllFollowingList(userId);

        // 팔로잉 유저들의 게시물 확인
        List<GetReadFeedDto> feedList = new ArrayList<>();
        for(FollowingUser user : followingList){
            List<GetReadFeedDto> feeds = feedRepository.readAll(user.getUserId());
            for(GetReadFeedDto x : feeds){
                feedList.add(x);
            }
        }

        // 리턴값 생성
        List<GetFollowingFeedResp> list = new ArrayList<>();
        for(GetReadFeedDto x : feedList) {
            String userImage = userDao.readImageById(x.getUserId());
            String username = userDao.readNameById(x.getUserId());

            // 피드에 좋아요 갯수 , 내가 좋아요 눌렀는지 확인
            List<LikeUser> userLikeList = feedLikesRepository.getUserLikeList(x.getFeedId());
            int likeCount = userLikeList.size();
            boolean like = false;
            for(LikeUser user : userLikeList){
                if(user.getUserId() == userId){
                    like = true;
                    break;
                }
            }

            int commentCount = commentRepository.countCommentByFeedId(x.getFeedId());

            GetFollowingFeedResp getFollowingFeedResp = GetFollowingFeedResp.builder()
                    .feedId(x.getFeedId())
                    .userId(x.getUserId())
                    .name(username)
                    .userImage(userImage)
                    .image(x.getImage())
                    .music(x.getMusic())
                    .imageCount(x.getCount())
                    .like(like)
                    .likeExist(x.getLikeFlag())
                    .likeCount(likeCount)
                    .content(x.getContent())
                    .commentExist(x.getCommentFlag())
                    .time(x.getUpdatedAt())
                    .commentCount(commentCount).build();

            list.add(getFollowingFeedResp);
        }

        List<GetReadFeedDto> getReadFeedDtos = feedRepository.readAll(userId);
        for(GetReadFeedDto x : getReadFeedDtos){
            String userImage = userDao.readImageById(x.getUserId());
            String username = userDao.readNameById(x.getUserId());

            List<LikeUser> userLikeList = feedLikesRepository.getUserLikeList(x.getFeedId());
            int likeCount = userLikeList.size();
            boolean like = false;
            for(LikeUser user : userLikeList){
                if(user.getUserId() == userId){
                    like = true;
                    break;
                }
            }
            int commentCount = commentRepository.countCommentByFeedId(x.getFeedId());

            GetFollowingFeedResp getFollowingFeedResp = GetFollowingFeedResp.builder()
                    .feedId(x.getFeedId())
                    .userId(x.getUserId())
                    .name(username)
                    .userImage(userImage)
                    .image(x.getImage())
                    .music(x.getMusic())
                    .imageCount(x.getCount())
                    .like(like)
                    .likeExist(x.getLikeFlag())
                    .likeCount(likeCount)
                    .content(x.getContent())
                    .commentExist(x.getCommentFlag())
                    .time(x.getUpdatedAt())
                    .commentCount(commentCount).build();

            list.add(getFollowingFeedResp);
        }

        Comparator<GetFollowingFeedResp> comparator = (o1, o2) -> o1.getTime().compareTo(o2.getTime());

        Collections.sort(list, comparator.reversed());

        return list;
    }


    @Transactional
    public List<GetReadFeedDto> AllFeed() throws BaseException {
        List<GetReadFeedDto> getReadFeedDtos = feedRepository.readAll();
        return getReadFeedDtos;
    }

    @Transactional
    public GetFeedDetailResp getFeedDetail(int idx, Integer feedId) throws BaseException {
        Feed feedEntity = feedRepository.readFeedById(feedId);

        GetUserRes feedWriter = userDao.getUser(feedEntity.getUserId());
        List<GetCommentListResp> commentList = getCommentList(idx, feedId);

        List<GetUserRes> commentWriter = new ArrayList<>();
        for(GetCommentListResp list : commentList){
            int userId = list.getUserId();
            GetUserRes user = userDao.getUser(userId);
            commentWriter.add(user);
        }

        GetFeedDetailResp getFeedDetailResp = GetFeedDetailResp.builder()
                .feedWriter(feedWriter)
                .feed(feedEntity)
                .commentWriter(commentWriter)
                .comment(commentList).build();

        return getFeedDetailResp;
    }

    @Transactional
    public int updateFeed(int feedId, String content) throws BaseException {
        int result = feedRepository.updateById(feedId, content);
        return result;
    }

    @Transactional
    public int deleteFeed(Integer feedId) throws BaseException {
        int result = feedRepository.deleteById(feedId);
        return result;
    }

    @Transactional
    public int showLikeNumber(Integer feedId, Integer flag) throws BaseException {
        int result = feedRepository.updateLikeStatusByFlag(feedId, flag);
        return result;
    }

    @Transactional
    public int hideLikeNumber(Integer feedId, Integer flag) throws BaseException {
        int result = feedRepository.updateLikeStatusByFlag(feedId, flag);
        return result;
    }

    @Transactional
    public int blockComment(int userId, Integer feedId, Integer flag) throws BaseException {
        Feed feedEntity = feedRepository.readFeedById(feedId);
        if(userId != feedEntity.getUserId()) throw new BaseException(BaseResponseStatus.INVALID_ACCESS);
        if(flag == feedEntity.getCommentFlag()) throw new BaseException(BaseResponseStatus.EQUAL_FLAG);

        int result = feedRepository.updateCommentStatusByFlag(feedId, flag);
        return result;
    }


    @Transactional
    public int createCommentLike(int userId, Integer commentId) throws BaseException {
        Comment comment = commentRepository.readOne(commentId);
        String content = comment.getContent();

        int result = commentLikeRepository.save(userId, commentId, content);
        return result;
    }

    public int deleteCommentLike(int userId, Integer commentId) throws BaseException {
        int result = commentLikeRepository.delete(userId, commentId);
        return result;
    }
}

package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.Constants.*;
import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.VideoDAO;
import com.proximity.assignment.model.Video;
import com.proximity.assignment.util.DBQueryUtil;
import com.proximity.assignment.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.proximity.assignment.commons.Constants.*;

/**
 * @author sthammishetty on 19/06/21
 */
@Repository
public class VideoDAOImpl extends AbstractDAOImpl<Video> implements VideoDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDAOImpl.class);

    public VideoDAOImpl() {
    }

    @Override
    public Long createVideo(String title, String link, Long userId) {
        JSONArray params = new JSONArray();
        params.put(0, title);
        params.put(1, link);
        params.put(2, userId);
        DBResult dbResult = DBQueryUtil.createEntity(VideoConstants.DB_VIDEO_TABLE_NAME, Arrays.asList(VideoConstants.DB_VIDEO_TILE_COLUMN, VideoConstants.DB_VIDEO_LINK_COLUMN, UserConstants.DB_USER_ID_COLUMN), params, VideoConstants.DB_VIDEO_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating video with title '{}', link '{}' userId '{}'  ", title, link, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created video with title '{}', link: {} with userId : {}", title, link, userId);
        return DBQueryUtil.getIdFromResult(dbResult, VideoConstants.DB_VIDEO_ID_COLUMN);
    }

    @Override
    public void deleteVideo(Long videoId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME).append(" WHERE ")
                .append(VideoConstants.DB_VIDEO_ID_COLUMN).append(" = ").append(videoId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete video: {}", videoId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully deleted video with videoId {}", videoId);
    }

    @Override
    public void updateVideo(Video video) {
        StringBuilder sb = new StringBuilder(" UPDATE ").append(VideoConstants.DB_VIDEO_TABLE_NAME).append(" SET ");

        List<String> setFields = new ArrayList<>();
        if (video.getLink() != null) {
            setFields.add(VideoConstants.DB_VIDEO_LINK_COLUMN + " = " + Utils.wrapWithSingleQuotes(video.getLink()));
        }

        if (video.getTitle() != null) {
            setFields.add(VideoConstants.DB_VIDEO_TILE_COLUMN + " = " + Utils.wrapWithSingleQuotes(video.getTitle()));
        }

        if (setFields.size() != 0) {
            sb.append(String.join(",", setFields));
            sb.append(" WHERE ").append(VideoConstants.DB_VIDEO_ID_COLUMN).append(" = ").append(video.getVideoId());
            DBResult dbResult = update(sb.toString());
            if (!dbResult.isSuccess()) {
                String msg = Utils.getMsg("Error updating video details of video with videoId'{}' ", video.getVideoId());
                LOGGER.error(msg, dbResult.getCause());
                throw new RuntimeException(msg, dbResult.getCause());
            }
            LOGGER.info(" Successfully updated video details of video '{}' ", video.getVideoId());
        }
    }


    @Override
    public Video get(Long id) {
        Video video = null;
        DBResult dbResult = DBQueryUtil.getEntityByEntityId(VideoConstants.DB_VIDEO_TABLE_NAME, VideoConstants.DB_VIDEO_ID_COLUMN, id);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting details of video : {}", id);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Video> videos = getVideos((JSONArray) dbResult.getResult());
        if (videos.size() > 0) {
            video = videos.get(0);
        }
        return video;
    }

    @Override
    public List<Video> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all videos");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getVideos(result);
    }

    @Override
    public void addVideoToLesson(Long videoId, Long lessonId) {
        JSONArray params = new JSONArray();
        params.put(0, videoId);
        params.put(1, lessonId);
        DBResult dbResult = DBQueryUtil.createEntity(DB_VIDEO_LESSONS_TABLE_NAME, Arrays.asList(VideoConstants.DB_VIDEO_ID_COLUMN, LessonConstants.DB_LESSON_ID_COLUMN), params, DB_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error adding  video : {} to lesson : {} ", videoId, lessonId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully added video '{}' to lesson '{}\"", videoId, lessonId);
    }

    @Override
    public void addTagToVideo(Long videoId, Long tagId) {
        JSONArray params = new JSONArray();
        params.put(0, videoId);
        params.put(1, tagId);
        DBResult dbResult = DBQueryUtil.createEntity(DB_VIDEO_TAGS_TABLE_NAME, Arrays.asList(VideoConstants.DB_VIDEO_ID_COLUMN, TagConstants.DB_TAG_ID_COLUMN), params, DB_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error adding tag '{}' to video : {}  ", tagId, videoId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully added tag: {} to video: {}", tagId, videoId);
    }

    @Override
    public Video getVideoByTitle(String title) {
        Video video = null;
        String query = new StringBuilder("SELECT * FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME).append(" WHERE ")
                .append(VideoConstants.DB_VIDEO_TILE_COLUMN).append(" = ").append(Utils.wrapWithSingleQuotes(title)).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting details of video with title : {}", title);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Video> videos = getVideos((JSONArray) dbResult.getResult());
        if (videos.size() > 0) {
            video = videos.get(0);
        }
        return video;
    }

    @Override
    public Video getVideoByTagId(Long tagId) {
        Video video = null;

        String inQuery = new StringBuilder(" SELECT ")
                .append(VideoConstants.DB_VIDEO_ID_COLUMN)
                .append(" FROM ").append(DB_VIDEO_TAGS_TABLE_NAME).append(" WHERE ").append(TagConstants.DB_TAG_ID_COLUMN)
                .append(" = ").append(tagId).toString();

        String query = new StringBuilder(" SELECT * FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME).append(" WHERE ")
                .append(VideoConstants.DB_VIDEO_ID_COLUMN).append(" IN ( ").append(inQuery).append(" )").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting video details with tagId : {}", tagId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Video> videos = getVideos((JSONArray) dbResult.getResult());
        if (videos.size() > 0) {
            video = videos.get(0);
        }
        return video;


    }

    @Override
    public Video getVideoByTagName(String title) {
        Video video = null;
        String inQuery = new StringBuilder(" SELECT ").append(TagConstants.DB_TAG_ID_COLUMN)
                .append(" FROM ").append(TagConstants.DB_TAGS_TABLE_NAME).append(" WHERE ")
                .append(TagConstants.DB_TAG_TITLE_COLUMN).append(" = ").append(Utils.wrapWithSingleQuotes(title)).toString();


        String videoIdQuery = new StringBuilder("SELECT ").append(VideoConstants.DB_VIDEO_ID_COLUMN)
                .append(" FROM ").append(DB_VIDEO_TAGS_TABLE_NAME).append(" WHERE ").append(TagConstants.DB_TAG_ID_COLUMN)
                .append(" IN ( ").append(inQuery).append(" )").toString();


        String query = new StringBuilder(" SELECT * FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME)
                .append(" WHERE ").append(VideoConstants.DB_VIDEO_ID_COLUMN).append(" IN ")
                .append("( ").append(videoIdQuery).append(" )").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting videos with tag title: {}", title);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Video> videos = getVideos((JSONArray) dbResult.getResult());
        if (videos.size() > 0) {
            video = videos.get(0);
        }
        return video;
    }

    @Override
    public List<Video> getAllActiveVideosByLesson(Long lessonId) {
        String inQuery = new StringBuilder(" SELECT ")
                .append(VideoConstants.DB_VIDEO_ID_COLUMN)
                .append(" FROM ").append(DB_VIDEO_LESSONS_TABLE_NAME).append(" WHERE ").append(LessonConstants.DB_LESSON_ID_COLUMN)
                .append(" = ").append(lessonId)
                .append(" AND ").append(DB_IS_ACTIVE_COLUMN).append(" = ").append(true).toString();

        String query = new StringBuilder(" SELECT * FROM ").append(VideoConstants.DB_VIDEO_TABLE_NAME).append(" WHERE ")
                .append(VideoConstants.DB_VIDEO_ID_COLUMN).append(" IN ( ").append(inQuery).append(" )").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting active videos for lesson {}", lessonId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        return getVideos((JSONArray) dbResult.getResult());
    }

    private List<Video> getVideos(JSONArray result) {
        List<Video> videos = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                Video video = new Video();
                video.setLink(jsonObject.getString(VideoConstants.DB_VIDEO_LINK_COLUMN));
                video.setTitle(jsonObject.getString(VideoConstants.DB_VIDEO_TILE_COLUMN));
                video.setVideoId(jsonObject.getLong(VideoConstants.DB_VIDEO_ID_COLUMN));
                video.setUserId(jsonObject.getLong(UserConstants.DB_USER_ID_COLUMN));
                videos.add(video);
            }
        }
        return videos;
    }
}

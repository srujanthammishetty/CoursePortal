package com.proximity.assignment.dao;

import com.proximity.assignment.model.Video;

import java.util.List;

/**
 * @author sthammishetty on 18/06/21
 */
public interface VideoDAO extends AbstractDAO<Video> {

    Long createVideo(String title, String link, Long userId);

    void deleteVideo(Long videoId);

    void updateVideo(Video video);

    void addVideoToLesson(Long videoId, Long lessonId);

    List<Video> getAllActiveVideosByLesson(Long lessonId);

    void addTagToVideo(Long videoId, Long tagId);

    Video getVideoByTitle(String title);

    Video getVideoByTagId(Long tagId);

    Video getVideoByTagName(String title);


}

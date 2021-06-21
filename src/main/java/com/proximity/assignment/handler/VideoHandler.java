package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.commons.Constants.LessonConstants;
import com.proximity.assignment.commons.Constants.TagConstants;
import com.proximity.assignment.commons.Constants.VideoConstants;
import com.proximity.assignment.dao.VideoDAO;
import com.proximity.assignment.model.Response;
import com.proximity.assignment.model.Video;
import com.proximity.assignment.util.Utils;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.proximity.assignment.commons.Constants.ACTION_TYPE;
import static com.proximity.assignment.commons.Constants.RESPONSE;

/**
 * @author sthammishetty on 20/06/21
 */
public class VideoHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoHandler.class);

    @Autowired
    VideoDAO videoDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(ACTION_TYPE);
        Response response = null;
        Long userId = Utils.getUserIdFromHeader(routingContext);
        switch (actionType) {
            case CREATE:
                response = createVideo(routingContext, userId);
                break;
            case GET:
                response = getVideo(routingContext);
                break;
            case UPDATE:
                response = updateVideo(routingContext, userId);
                break;
            case DELETE:
                response = deleteVideo(routingContext, userId);
                break;
            case LIST_ALL:
                response = getAllVideos();
                break;

            case ADD_VIDEO_TO_LESSON:
                response = addVideoToLesson(routingContext, userId);
                break;
            case ADD_TAG_TO_VIDEO:
                response = addTagToVideo(routingContext, userId);
                break;
            case VIDEO_BY_TITLE:
                response = getVideoByTitle(routingContext);
                break;
            case VIDEO_BY_TAG:
                response = getVideoByTagName(routingContext);
                break;
            default:
                break;
        }

        if (response != null) {
            response.setSuccess(true);
            routingContext.put(RESPONSE, response);
        }
        routingContext.next();
    }

    private Response getVideoByTagName(RoutingContext routingContext) {
        Response response = new Response();
        String tagTitle = routingContext.request().getHeader(TagConstants.TAG_TITLE_KEY);
        Video video = videoDAO.getVideoByTagName(tagTitle);
        response.setResult(video);
        return response;
    }

    private Response getVideoByTitle(RoutingContext routingContext) {
        Response response = new Response();
        String title = routingContext.request().getHeader(VideoConstants.VIDEO_TITLE_KEY);
        Video video = videoDAO.getVideoByTitle(title);
        response.setResult(video);
        return response;
    }

    private Response addTagToVideo(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        JsonObject body = routingContext.getBodyAsJson();
        Long tagId = body.getLong(TagConstants.TAG_ID_KEY);
        Long videoId = body.getLong(VideoConstants.VIDEO_ID_KEY);
        videoDAO.addTagToVideo(videoId, tagId);
        String msg = Utils.getMsg("Added tag '{}' to video '{}' by user '{}' ", tagId, videoId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response addVideoToLesson(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        JsonObject bodyAsJson = routingContext.getBodyAsJson();
        Long videoId = bodyAsJson.getLong(VideoConstants.VIDEO_ID_KEY);
        Long lessonId = bodyAsJson.getLong(LessonConstants.LESSON_ID_KEY);
        videoDAO.addVideoToLesson(videoId, lessonId);
        String msg = Utils.getMsg("Video '{}' to lesson '{}' by user '{}' ", videoId, lessonId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getAllVideos() {
        Response response = new Response();
        response.setResult(videoDAO.listAll());
        return response;
    }

    private Response deleteVideo(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long videoId = Utils.getVideoIdFromHeader(routingContext);
        videoDAO.deleteVideo(videoId);
        String msg = Utils.getMsg("Video '{}' deleted by user '{}' ", videoId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response updateVideo(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Video video = Utils.convertJsonToObject(routingContext.getBodyAsString(), Video.class);
        videoDAO.updateVideo(video);
        String msg = Utils.getMsg("Video '{}' details updated by user '{}'", video.getVideoId(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getVideo(RoutingContext routingContext) {
        Response response = new Response();
        Long videoId = Utils.getVideoIdFromHeader(routingContext);
        Video video = videoDAO.get(videoId);
        response.setResult(video);
        return response;
    }

    private Response createVideo(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Video video = Utils.convertJsonToObject(routingContext.getBodyAsString(), Video.class);
        videoDAO.createVideo(video.getTitle(), video.getLink(), userId);
        String msg = Utils.getMsg("Created video with title '{}', link '{}' by user '{}' ", video.getTitle(), video.getLink(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }
}

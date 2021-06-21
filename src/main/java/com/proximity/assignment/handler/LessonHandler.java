package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.Constants.CourseConstants;
import com.proximity.assignment.dao.LessonDAO;
import com.proximity.assignment.model.Lesson;
import com.proximity.assignment.model.Response;
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
public class LessonHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonHandler.class);

    @Autowired
    LessonDAO lessonDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(ACTION_TYPE);
        Response response = null;
        Long userId = Utils.getUserIdFromHeader(routingContext);

        switch (actionType) {
            case CREATE:
                response = createLesson(routingContext, userId);
                break;

            case GET:
                response = getLesson(routingContext);
                break;

            case UPDATE:
                response = updateLesson(routingContext, userId);
                break;

            case DELETE:
                response = deleteLesson(routingContext, userId);
                break;

            case LIST_ALL:
                response = listLessons();
                break;

            case ACTIVE_LESSONS_IN_COURSE:
                response = getActiveLessonsInCourse(routingContext);
                break;

            case ADD_LESSON_TO_COURSE:
                response = addLessonToCourse(routingContext, userId);
                break;

            case LESSON_ACTIVE_STATUS:
                response = updateLessonActiveStatus(routingContext, userId);
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

    private Response updateLessonActiveStatus(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        JsonObject bodyAsJson = routingContext.getBodyAsJson();
        Long courseId = bodyAsJson.getLong(CourseConstants.COURSE_ID_KEY);
        Long lessonId = bodyAsJson.getLong(Constants.LessonConstants.LESSON_ID_KEY);
        boolean status = bodyAsJson.getBoolean(Constants.ACTIVE_KEY);
        lessonDAO.updateLessonActiveStatus(lessonId, courseId, status);
        String msg = Utils.getMsg("Updated active status of lesson '{}' in course: '{}' to '{}' by user '{}'", lessonId, courseId, status, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response addLessonToCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long lessonId = Utils.getLessonIdFromHeader(routingContext);
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        lessonDAO.addLessonToCourse(lessonId, courseId);
        String msg = Utils.getMsg("Added lesson '{}' to course '{} by user '{}", lessonId, courseId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getActiveLessonsInCourse(RoutingContext routingContext) {
        Response response = new Response();
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        response.setResult(lessonDAO.getActiveLessons(courseId));
        return response;
    }

    private Response listLessons() {
        Response response = new Response();
        response.setResult(lessonDAO.listAll());
        return response;
    }

    private Response deleteLesson(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long lessonId = Utils.getLessonIdFromHeader(routingContext);
        lessonDAO.deleteLesson(lessonId);
        String msg = Utils.getMsg("Lesson '{}' deleted by user '{}' ", lessonId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response updateLesson(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Lesson lesson = Utils.convertJsonToObject(routingContext.getBodyAsString(), Lesson.class);
        lessonDAO.updateLesson(lesson);
        String msg = Utils.getMsg("Updated lesson '{}' to '{} by user '{}", lesson.getLessonId(), lesson.getName(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getLesson(RoutingContext routingContext) {
        Response response = new Response();
        Long lessonId = Utils.getLessonIdFromHeader(routingContext);
        Lesson lesson = lessonDAO.get(lessonId);
        response.setResult(lesson);
        return response;
    }

    private Response createLesson(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Lesson lesson = Utils.convertJsonToObject(routingContext.getBodyAsString(), Lesson.class);
        lessonDAO.createLesson(lesson.getName(), userId);
        String msg = Utils.getMsg("Lesson '{}' created by user '{}' ", lesson.getName(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }
}

package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.dao.CourseDAO;
import com.proximity.assignment.model.Course;
import com.proximity.assignment.model.Response;
import com.proximity.assignment.util.Utils;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.proximity.assignment.commons.Constants.ACTION_TYPE;
import static com.proximity.assignment.commons.Constants.RESPONSE;

/**
 * @author sthammishetty on 20/06/21
 */
public class CourseHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseHandler.class);

    @Autowired
    CourseDAO courseDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(ACTION_TYPE);
        Response response = null;
        Long userId = Utils.getUserIdFromHeader(routingContext);

        switch (actionType) {
            case CREATE:
                response = createCourse(routingContext, userId);
                break;
            case GET:
                response = getCourse(routingContext);
                break;
            case DELETE:
                response = deleteCourse(routingContext, userId);
                break;
            case UPDATE:
                response = updateCourse(routingContext, userId);
                break;
            case LIST_ALL:
                response = getAllCourses();
                break;
            case SUBSCRIBE_COURSE:
                response = subscribeCourse(routingContext, userId);
                break;
            case UN_SUBSCRIBE_COURSE:
                response = unsubscribeCourse(routingContext, userId);
                break;
            case ACTIVE:
                response = getActiveCourses();
                break;
            case COURSES_SUBJECT_ID:
                response = getCoursesWithSubject(routingContext);
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

    private Response getCoursesWithSubject(RoutingContext routingContext) {
        Response response = new Response();
        Long subjectID = Utils.getSubjectIdFromHeader(routingContext);
        response.setResult(courseDAO.getCourses(subjectID));
        return response;
    }

    private Response getActiveCourses() {
        Response response = new Response();
        response.setResult(courseDAO.getAllActiveCourses());
        return response;
    }

    private Response unsubscribeCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        courseDAO.unsubscribeToCourse(userId, courseId);
        String msg = Utils.getMsg("Un-Subscribed course '{}' for user '{}'", courseId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response subscribeCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        courseDAO.subscribeToCourse(userId, courseId);
        String msg = Utils.getMsg("Subscribed course '{}' for user '{}'", courseId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response updateCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        String body = routingContext.getBodyAsString();
        Course course = Utils.convertJsonToObject(body, Course.class);
        courseDAO.updateCourse(course);
        String msg = Utils.getMsg("Successfully updated course: {} by user: {}", course, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response deleteCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        courseDAO.deleteCourse(courseId);
        String msg = Utils.getMsg("Successfully deleted course '{}' by user '{}\"", courseId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getAllCourses() {
        Response response = new Response();
        response.setResult(courseDAO.listAll());
        return response;
    }

    private Response getCourse(RoutingContext routingContext) {
        Response response = new Response();
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        Course course = courseDAO.get(courseId);
        response.setResult(course);
        return response;
    }

    private Response createCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Course course = Utils.convertJsonToObject(routingContext.getBodyAsString(), Course.class);
        courseDAO.createCourse(course.getName(), userId);
        String msg = Utils.getMsg("Successfully created course: {} by user: {}", course.getName(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }
}

package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.dao.SubjectDAO;
import com.proximity.assignment.model.Response;
import com.proximity.assignment.model.Subject;
import com.proximity.assignment.util.Utils;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.proximity.assignment.commons.Constants.ACTION_TYPE;
import static com.proximity.assignment.commons.Constants.RESPONSE;

/**
 * @author sthammishetty on 20/06/21
 */
public class SubjectHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectHandler.class);

    @Autowired
    SubjectDAO subjectDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(ACTION_TYPE);
        Response response = null;
        Long userId = Utils.getUserIdFromHeader(routingContext);
        switch (actionType) {
            case CREATE:
                response = createSubject(routingContext, userId);
                break;

            case DELETE:
                response = deleteSubject(routingContext, userId);
                break;

            case GET:
                response = getSubject(routingContext);
                break;

            case UPDATE:
                response = updateSubject(routingContext, userId);
                break;

            case LIST_ALL:
                response = listAllSubjects();
                break;

            case ADD_SUBJECT_TO_COURSE:
                response = addSubjectToCourse(routingContext, userId);
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

    private Response addSubjectToCourse(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long subjectId = Utils.getSubjectIdFromHeader(routingContext);
        Long courseId = Utils.getCourseIdFromHeader(routingContext);
        subjectDAO.addSubjectToCourse(courseId, subjectId);
        String msg = Utils.getMsg("Added subject '{}' to course '{}' by user '{}' ", subjectId, courseId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response listAllSubjects() {
        Response response = new Response();
        List<Subject> subjects = subjectDAO.listAll();
        response.setResult(subjects);
        return response;
    }

    private Response updateSubject(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Subject subject = Utils.convertJsonToObject(routingContext.getBodyAsString(), Subject.class);
        subjectDAO.updateSubject(subject);
        String msg = Utils.getMsg("Subject '{}' updated to '{}', by user '{}' . '{}' ", subject.getSubjectId(), subject, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getSubject(RoutingContext routingContext) {
        Response response = new Response();
        Long subjectId = Utils.getSubjectIdFromHeader(routingContext);
        Subject subject = subjectDAO.get(subjectId);
        response.setResult(subject);
        return response;
    }

    private Response deleteSubject(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long subjectId = Utils.getSubjectIdFromHeader(routingContext);
        subjectDAO.deleteSubject(subjectId);
        String msg = Utils.getMsg("Subject '{}' is deleted by user '{}' ", subjectId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response createSubject(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Subject subject = Utils.convertJsonToObject(routingContext.getBodyAsString(), Subject.class);
        subjectDAO.createSubject(subject.getName(), userId);
        String msg = Utils.getMsg("Subject '{}' created by user '{}' ", subject.getName(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }
}

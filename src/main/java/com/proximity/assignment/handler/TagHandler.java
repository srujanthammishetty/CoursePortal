package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.dao.TagDAO;
import com.proximity.assignment.model.Response;
import com.proximity.assignment.model.Tag;
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
public class TagHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagHandler.class);

    @Autowired
    TagDAO tagDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(ACTION_TYPE);
        Response response = null;
        Long userId = Utils.getUserIdFromHeader(routingContext);
        switch (actionType) {
            case CREATE:
                response = createTag(routingContext, userId);
                break;
            case GET:
                response = getTag(routingContext);
                break;
            case UPDATE:
                response = updateTag(routingContext, userId);
                break;
            case DELETE:
                response = deleteTag(routingContext, userId);
                break;

            case LIST_ALL:
                response = listAllTags(routingContext);
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

    private Response listAllTags(RoutingContext routingContext) {
        Response response = new Response();
        response.setResult(tagDAO.listAll());
        return response;
    }

    private Response deleteTag(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Long tagId = Utils.getTagIdFromHeader(routingContext);
        tagDAO.deleteTag(tagId);
        String msg = Utils.getMsg("Tag '{}' deleted by user with userId '{}' ", tagId, userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response updateTag(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Tag tag = Utils.convertJsonToObject(routingContext.getBodyAsString(), Tag.class);
        tagDAO.updateTag(tag);
        String msg = Utils.getMsg("Tag '{}' updated by user '{}' to '{}' ", tag.getTagId(), userId, tag);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getTag(RoutingContext routingContext) {
        Response response = new Response();
        Long tagId = Utils.getTagIdFromHeader(routingContext);
        Tag tag = tagDAO.get(tagId);
        response.setResult(tag);
        return response;
    }

    private Response createTag(RoutingContext routingContext, Long userId) {
        Response response = new Response();
        Tag tag = Utils.convertJsonToObject(routingContext.getBodyAsString(), Tag.class);
        tagDAO.createTag(tag.getTitle(), userId);
        String msg = Utils.getMsg("Created tag '{}' by user '{}' ", tag.getTitle(), userId);
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }
}

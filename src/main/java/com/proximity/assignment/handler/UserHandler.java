package com.proximity.assignment.handler;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.Response;
import com.proximity.assignment.model.User;
import com.proximity.assignment.util.Utils;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sthammishetty on 20/06/21
 */
public class UserHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

    @Autowired
    UserDAO userDAO;

    @Override
    public void handle(RoutingContext routingContext) {
        ActionType actionType = routingContext.get(Constants.ACTION_TYPE);
        Response response = null;
        switch (actionType) {
            case CREATE:
                response = createUser(routingContext);
                break;
            case GET:
                response = getUser(routingContext);
                break;

            case LIST_ALL:
                response = listAllUsers(routingContext);

            default:
                break;
        }

        if (response != null) {
            response.setSuccess(true);
            routingContext.put(Constants.RESPONSE, response);
        }
        routingContext.next();
    }

    private Response listAllUsers(RoutingContext routingContext) {
        Response response = new Response();
        response.setResult(userDAO.listAll());
        return response;
    }

    private Response createUser(RoutingContext routingContext) {
        Response response = new Response();
        User user = Utils.convertJsonToObject(routingContext.getBodyAsString(), User.class);
        userDAO.createUser(user.getName(), user.getEmailId(), user.isInstructor());
        String msg = Utils.getMsg("Successfully created user with name '{}', with isInstructor flag set to '{}' ", user.getName(), user.isInstructor());
        LOGGER.info(msg);
        response.setResult(msg);
        return response;
    }

    private Response getUser(RoutingContext routingContext) {
        Response response = new Response();
        User user = userDAO.get(Long.valueOf(routingContext.request().getHeader(Constants.USER_ID_HEADER)));
        response.setResult(user);
        return response;
    }


}

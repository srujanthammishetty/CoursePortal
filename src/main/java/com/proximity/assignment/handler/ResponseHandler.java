package com.proximity.assignment.handler;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.model.Response;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Objects;

/**
 * @author sthammishetty on 20/06/21
 */


public class ResponseHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext event) {

        int statusCode = event.response().getStatusCode();
        if (statusCode <= 0) {
            statusCode = HttpStatus.OK.value();
        }

        Response response = event.get(Constants.RESPONSE);
        if (response == null) {
            response = new Response();
            response.setResult(false);
            response.setErrorDesc("Bad request, invalid endpoint");
            statusCode = HttpStatus.BAD_REQUEST.value();
        }
        if (statusCode != HttpStatus.BAD_REQUEST.value()) {
            if(Objects.isNull(response.getResult())){
                response.setResult(Collections.EMPTY_MAP);
            }
        }

        HttpServerResponse httpResponse = event.response();
        httpResponse.putHeader("content-type", "application/json");
        httpResponse.setStatusCode(statusCode).end(Json.encodePrettily(response));
    }

}

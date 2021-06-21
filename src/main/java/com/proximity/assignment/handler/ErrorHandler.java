package com.proximity.assignment.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.springframework.http.HttpStatus;

/**
 * @author sthammishetty on 21/06/21
 */
public class ErrorHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext event) {
        Throwable failure = event.failure();
        HttpServerResponse httpResponse = event.response();
        httpResponse.putHeader("content-type", "application/json");
        httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).end(failure.getMessage());
    }
}

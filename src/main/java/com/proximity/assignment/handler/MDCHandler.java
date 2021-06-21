package com.proximity.assignment.handler;

import com.proximity.assignment.commons.Constants;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.MDC;

/**
 * @author sthammishetty on 20/06/21
 */
public class MDCHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        MDC.clear();
        MDC.put("userId", event.request().getHeader(Constants.USER_ID_HEADER));
        event.next();
    }
}

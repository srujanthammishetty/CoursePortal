package com.proximity.assignment.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sthammishetty on 20/06/21
 */
public class SetupVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupVerticle.class);

    @Autowired
    private SpringVerticleFactory verticleFactory;

    @Override
    public void start(Future<Void> future) {

        vertx.executeBlocking(applicationFuture -> {
            vertx.registerVerticleFactory(verticleFactory);
            deployApiVerticle().setHandler(result -> {
                if (result.succeeded()) {
                    applicationFuture.complete();
                } else {
                    applicationFuture.fail(result.cause());
                }
            });
        }, result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                LOGGER.error("Failed to deploy SetupVerticle");
                vertx.close();
                future.fail(result.cause());
            }
        });


    }

    private Future<Void> deployApiVerticle() {
        Future<Void> future = Future.future();
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        String apiVerticle = verticleFactory.prefix() + ":" + ApiVerticle.class.getName();
        vertx.deployVerticle(apiVerticle, deploymentOptions, result -> {
            if (result.succeeded()) {
                LOGGER.info("ApiVerticle deployed successfully");
                future.complete();
            } else {
                LOGGER.error("Failed to deploy ApiVerticle  {}", result.cause());
                future.fail(result.cause());
            }
        });
        return future;
    }

}

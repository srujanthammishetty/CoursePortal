package com.proximity.assignment;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.util.DBServiceUtil;
import com.proximity.assignment.verticles.SetupVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;

/**
 * @author sthammishetty on 20/06/21
 */
@SpringBootApplication(scanBasePackages = "com.proximity.assignment.*", exclude = {WebMvcAutoConfiguration.class})
public class PortalApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PortalApplication.class, args);
        PropertiesService.loadPropertiesFromFileSystem();
        LOGGER.info("Successfully Loaded properties from deployment.properties");
        setUpShutDownHook();
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(context.getBean(SetupVerticle.class), result -> {
            if (result.failed()) {
                vertx.close();
                SpringApplication.exit(context);
            }
        });

    }

    private static void setUpShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Inside shutDownHook ");
            LOGGER.info("Closing all db connections");
            DBServiceUtil.closeDbConnections();
        }));
    }

}

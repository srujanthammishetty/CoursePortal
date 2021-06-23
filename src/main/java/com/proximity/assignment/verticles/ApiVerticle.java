package com.proximity.assignment.verticles;

import com.proximity.assignment.api.ActionType;
import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.handler.*;
import com.proximity.assignment.util.DBServiceUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.vertx.ext.web.handler.BodyHandler.create;

/**
 * @author sthammishetty on 20/06/21
 */
public class ApiVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

    private static final String ALL_APIS = "/app/*";
    public static final String API_BASE_URL_PREFIX = "/app/portal";
    public static final String USER_API_URL_PREFIX = API_BASE_URL_PREFIX + "/user";
    public static final String COURSE_API_URL_PREFIX = API_BASE_URL_PREFIX + "/course";
    public static final String SUBJECT_API_URL_PREFIX = API_BASE_URL_PREFIX + "/subject";
    public static final String LESSON_API_URL_PREFIX = API_BASE_URL_PREFIX + "/lesson";
    public static final String VIDEO_API_URL_PREFIX = API_BASE_URL_PREFIX + "/video";
    public static final String TAG_API_URL_PREFIX = API_BASE_URL_PREFIX + "/tag";


    @Autowired
    private UserHandler userHandler;

    @Autowired
    private CourseHandler courseHandler;

    @Autowired
    private SubjectHandler subjectHandler;

    @Autowired
    private LessonHandler lessonHandler;

    @Autowired
    private VideoHandler videoHandler;

    @Autowired
    private TagHandler tagHandler;

    @Autowired
    private ResponseHandler responseHandler;

    @Autowired
    private MDCHandler mdcHandler;

    @Autowired
    private ErrorHandler errorHandler;

    @Override
    public void start(Future<Void> future) {
        DBServiceUtil.initializeHikariDataSource();
        vertx.<HttpServer>executeBlocking(startServerFuture -> {
            Router router = Router.router(vertx);
            registerRoutes(router);
            createHttpServer(router).setHandler(startServerFuture);
        }, result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                LOGGER.error("Failed to create http server", result.cause());
                future.fail(result.cause());
            }
        });
    }

    private void registerRoutes(Router router) {

        router.route(ALL_APIS).failureHandler(errorHandler);
        router.route(ALL_APIS).handler(create());

        router.route(ALL_APIS).blockingHandler(mdcHandler);


        //create user
        router.post(USER_API_URL_PREFIX).blockingHandler(context -> userHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get user by userId
        router.get(USER_API_URL_PREFIX).blockingHandler(context -> userHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //list all users
        router.get(USER_API_URL_PREFIX + "/all").blockingHandler(context -> userHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));


        //create course
        router.post(COURSE_API_URL_PREFIX).blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get course
        router.get(COURSE_API_URL_PREFIX).blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //update course
        router.put(COURSE_API_URL_PREFIX).blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UPDATE)));
        //delete course
        router.delete(COURSE_API_URL_PREFIX).blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.DELETE)));
        //get all courses
        router.get(COURSE_API_URL_PREFIX + "/all").blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));
        //subscribe to course
        router.put(COURSE_API_URL_PREFIX + "/subscribe").blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.SUBSCRIBE_COURSE)));
        //unsubscribe to course
        router.put(COURSE_API_URL_PREFIX + "/unsubscribe").blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UN_SUBSCRIBE_COURSE)));
        //active courses
        router.get(COURSE_API_URL_PREFIX + "/activeCourses").blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ACTIVE)));
        //get courses that contain given subject
        router.get(COURSE_API_URL_PREFIX + "/subject").blockingHandler(context -> courseHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.COURSES_SUBJECT_ID)));


        //create subject
        router.post(SUBJECT_API_URL_PREFIX).blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get subject
        router.get(SUBJECT_API_URL_PREFIX).blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //update subject
        router.put(SUBJECT_API_URL_PREFIX).blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UPDATE)));
        //delete subject
        router.delete(SUBJECT_API_URL_PREFIX).blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.DELETE)));
        //get all subjects
        router.get(SUBJECT_API_URL_PREFIX + "/all").blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));
        //add subject to course
        router.post(SUBJECT_API_URL_PREFIX + "/add").blockingHandler(context -> subjectHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ADD_SUBJECT_TO_COURSE)));


        //create lesson
        router.post(LESSON_API_URL_PREFIX).blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get lesson
        router.get(LESSON_API_URL_PREFIX).blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //update lesson
        router.put(LESSON_API_URL_PREFIX).blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UPDATE)));
        //delete lesson
        router.delete(LESSON_API_URL_PREFIX).blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.DELETE)));
        //get all lessons
        router.get(LESSON_API_URL_PREFIX + "/all").blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));
        //get active lessons in given course
        router.get(LESSON_API_URL_PREFIX + "/active").blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ACTIVE_LESSONS_IN_COURSE)));
        //add subject to course
        router.post(LESSON_API_URL_PREFIX + "/add").blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ADD_LESSON_TO_COURSE)));
        //update active status of lesson in given course
        router.put(LESSON_API_URL_PREFIX + "/status").blockingHandler(context -> lessonHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LESSON_ACTIVE_STATUS)));


        //create tag
        router.post(TAG_API_URL_PREFIX).blockingHandler(context -> tagHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get tag
        router.get(TAG_API_URL_PREFIX).blockingHandler(context -> tagHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //update tag
        router.put(TAG_API_URL_PREFIX).blockingHandler(context -> tagHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UPDATE)));
        //delete tag
        router.delete(TAG_API_URL_PREFIX).blockingHandler(context -> tagHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.DELETE)));
        //get all tags
        router.get(TAG_API_URL_PREFIX + "/all").blockingHandler(context -> tagHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));


        //create video
        router.post(VIDEO_API_URL_PREFIX).blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.CREATE)));
        //get video
        router.get(VIDEO_API_URL_PREFIX).blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.GET)));
        //update video
        router.put(VIDEO_API_URL_PREFIX).blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.UPDATE)));
        //delete video
        router.delete(VIDEO_API_URL_PREFIX).blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.DELETE)));
        //get all videos
        router.get(VIDEO_API_URL_PREFIX + "/all").blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.LIST_ALL)));
        //add video to lesson
        router.post(VIDEO_API_URL_PREFIX + "/add/lesson").blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ADD_VIDEO_TO_LESSON)));
        //add tag to video
        router.post(VIDEO_API_URL_PREFIX + "/add/tag").blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.ADD_TAG_TO_VIDEO)));
        //get video by title
        router.get(VIDEO_API_URL_PREFIX + "/title").blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.VIDEO_BY_TITLE)));
        //get video by tag
        router.get(VIDEO_API_URL_PREFIX + "/tag").blockingHandler(context -> videoHandler.handle(context.put(Constants.ACTION_TYPE, ActionType.VIDEO_BY_TAG)));


        router.route(ALL_APIS).blockingHandler(responseHandler);
    }


    private Future<HttpServer> createHttpServer(Router router) {
        HttpServerOptions httpServerOptions = new HttpServerOptions();
        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(
                        Integer.valueOf(PropertiesService.getValue(Constants.APPLICATION_PORT, "8091")),
                        httpServerFuture);

        return httpServerFuture;
    }
}

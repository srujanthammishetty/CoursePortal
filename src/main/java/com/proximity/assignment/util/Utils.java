package com.proximity.assignment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proximity.assignment.commons.Constants.*;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import static com.proximity.assignment.commons.Constants.VideoConstants.VIDEO_ID_KEY;

/**
 * @author sthammishetty on 18/06/21
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getMsg(String msg, Object... args) {
        return MessageFormatter.arrayFormat(msg, args).getMessage();
    }

    public static String wrapWithRoundBracket(String msg) {
        return "( " + msg + " )";
    }

    public static String wrapWithSingleQuotes(String msg) {
        return "'" + msg + "'";
    }

    public static <T> T convertJsonToObject(String json, Class<T> t) {
        T object = null;
        try {
            object = objectMapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting json to object", e);
        }
        return object;
    }

    public static Long getUserIdFromHeader(RoutingContext routingContext) {
        String header = routingContext.request().getHeader(UserConstants.USER_ID_KEY);
        return Long.valueOf(StringUtils.defaultIfBlank(header,"0"));
    }

    public static Long getCourseIdFromHeader(RoutingContext routingContext) {
        return Long.valueOf(routingContext.request().getHeader(CourseConstants.COURSE_ID_KEY));
    }

    public static Long getSubjectIdFromHeader(RoutingContext routingContext) {
        return Long.valueOf(routingContext.request().getHeader(SubjectConstants.SUBJECT_ID_KEY));
    }

    public static Long getLessonIdFromHeader(RoutingContext routingContext) {
        return Long.valueOf(routingContext.request().getHeader(LessonConstants.LESSON_ID_KEY));
    }

    public static Long getVideoIdFromHeader(RoutingContext routingContext) {
        return Long.valueOf(routingContext.request().getHeader(VIDEO_ID_KEY));
    }

    public static Long getTagIdFromHeader(RoutingContext routingContext) {
        return Long.valueOf(routingContext.request().getHeader(TagConstants.TAG_ID_KEY));
    }


}

package com.proximity.assignment.api;

/**
 * @author sthammishetty on 20/06/21
 */
public enum ActionType {

    SUBSCRIBE_COURSE("subscribe"),
    UN_SUBSCRIBE_COURSE("unsubscribe"),
    COURSES_SUBJECT_ID("coursesSubjectId"),


    ADD_LESSON_TO_COURSE("addLessonToCourse"),
    LESSON_ACTIVE_STATUS("lessonActiveStatus"),
    ACTIVE_LESSONS_IN_COURSE("activeLessonInCourse"),


    ADD_SUBJECT_TO_COURSE("addSubjectToCourse"),

    ADD_VIDEO_TO_LESSON("addVideoToLesson"),
    ADD_TAG_TO_VIDEO("addTagToVideo"),
    VIDEO_BY_TITLE("videoByTitle"),
    VIDEO_BY_TAG("videoByTag"),


    ACTIVE("active"),
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST_ALL("listAll"),
    GET("get");

    private String name;

    ActionType(String name) {
        this.name = name;
    }
}

package com.proximity.assignment.commons;

/**
 * @author sthammishetty on 18/06/21
 */
public class Constants {

    public static final String DB_URL = "db.url";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_DRIVER_CLASS_NAME = "db.driver.class.name";
    public static final String DB_CONNECTION_POOL_SIZE = "db.connection.pool.size";
    public static final String SERVER_PORT = "server.port";

    public static final String DEPLOYMENT_PROPERTIES = "deployment.properties";

    public static final String DB_IS_ACTIVE_COLUMN = "is_active";
    public static final String DB_ID_COLUMN = "id";

    public static final String ACTIVE_KEY = "active";
    public static final String ACTION_TYPE = "actionType";
    public static final String RESPONSE = "response";
    public static final String USER_ID_HEADER = "userId";
    public static final String QUESTION_MARK = "?";

    public static class UserConstants {
        public static final String DB_USERS_TABLE_NAME = "users";
        public static final String DB_USER_ID_COLUMN = "user_id";
        public static final String DB_IS_INSTRUCTOR_COLUMN = "is_instructor";
        public static final String DB_USER_NAME_COLUMN = "name";
        public static final String DB_USER_EMAIL_ID_COLUMN = "email_id";

        public static final String USER_ID_KEY = "userId";
        public static final String USER_NAME_KEY = "name";
        public static final String IS_INSTRUCTOR_KEY = "isInstructor";
        public static final String EMAIL_ID = "emailId";
        public static final String USER = "user";
    }

    public static class CourseConstants {
        public static final String DB_COURSES_TABLE_NAME = "courses";
        public static final String DB_COURSE_ID_COLUMN = "course_id";
        public static final String DB_COURSE_NAME_COLUMN = "name";
        public static final String COURSE = "course";

        public static final String COURSE_NAME_KEY = "name";
        public static final String COURSE_ID_KEY = "courseId";
    }

    public static class SubjectConstants {
        public static final String DB_SUBJECTS_TABLE_NAME = "subjects";
        public static final String DB_SUBJECT_ID_COLUMN = "subject_id";
        public static final String DB_SUBJECT_NAME_COLUMN = "name";
        public static final String SUBJECT = "subject";

        public static final String SUBJECT_NAME_KEY = "name";
        public static final String SUBJECT_ID_KEY = "subjectId";

    }

    public static class LessonConstants {
        public static final String DB_LESSON_TABLE_NAME = "lessons";
        public static final String DB_LESSON_ID_COLUMN = "lesson_id";
        public static final String DB_LESSON_NAME_COLUMN = "name";
        public static final String LESSON = "lesson";

        public static final String LESSON_NAME_KEY = "name";
        public static final String LESSON_ID_KEY = "lessonId";

    }

    public static class VideoConstants {
        public static final String DB_VIDEO_TABLE_NAME = "videos";
        public static final String DB_VIDEO_ID_COLUMN = "video_id";
        public static final String DB_VIDEO_LINK_COLUMN = "link";
        public static final String DB_VIDEO_TILE_COLUMN = "title";
        public static final String VIDEO = "video";


        public static final String VIDEO_LINK_KEY = "link";
        public static final String VIDEO_TITLE_KEY = "title";
        public static final String VIDEO_ID_KEY = "videoId";

    }

    public static class TagConstants {
        public static final String DB_TAGS_TABLE_NAME = "tags";
        public static final String DB_TAG_ID_COLUMN = "tag_id";
        public static final String DB_TAG_TITLE_COLUMN = "title";
        public static final String TAG = "tag";

        public static final String TAG_TITLE_KEY = "title";
        public static final String TAG_ID_KEY = "tagId";

    }

    public static final String DB_COURSE_SUBJECT_TABLE_NAME = "course_subjects";
    public static final String DB_LESSON_COURSES_TABLE_NAME = "lesson_courses";
    public static final String DB_STUDENT_COURSES_TABLE_NAME = "student_courses";
    public static final String DB_VIDEO_LESSONS_TABLE_NAME = "video_lessons";
    public static final String DB_VIDEO_TAGS_TABLE_NAME = "video_tags";

}

package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.Constants.CourseConstants;
import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.LessonDAO;
import com.proximity.assignment.model.Lesson;
import com.proximity.assignment.util.DBQueryUtil;
import com.proximity.assignment.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.proximity.assignment.commons.Constants.DB_LESSON_COURSES_TABLE_NAME;
import static com.proximity.assignment.commons.Constants.LessonConstants;
import static com.proximity.assignment.util.Utils.getMsg;

/**
 * @author sthammishetty on 19/06/21
 */
@Repository
public class LessonDAOImpl extends AbstractDAOImpl<Lesson> implements LessonDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonDAOImpl.class);

    public LessonDAOImpl() {
    }

    @Override
    public Long createLesson(String name, Long userId) {
        JSONArray params = new JSONArray();
        params.put(0, name);
        params.put(1, userId);
        DBResult dbResult = DBQueryUtil.createEntity(LessonConstants.DB_LESSON_TABLE_NAME, Arrays.asList(LessonConstants.DB_LESSON_NAME_COLUMN, Constants.UserConstants.DB_USER_ID_COLUMN), params, LessonConstants.DB_LESSON_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating lesson '{}', userId '{}'  ", name, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created lesson '{}' for instructor with userId '{}' ", name, userId);
        return DBQueryUtil.getIdFromResult(dbResult, LessonConstants.DB_LESSON_ID_COLUMN);
    }

    @Override
    public void deleteLesson(Long lessonId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(LessonConstants.DB_LESSON_TABLE_NAME).append(" WHERE ")
                .append(LessonConstants.DB_LESSON_ID_COLUMN).append(" = ").append(lessonId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete lesson {}", lessonId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully deleted lesson with lessonId {}", lessonId);
    }

    @Override
    public void updateLesson(Lesson lesson) {
        StringBuilder sb = new StringBuilder(" UPDATE ").append(LessonConstants.DB_LESSON_TABLE_NAME).append(" SET ");

        List<String> setFields = new ArrayList<>();
        if (lesson.getName() != null) {
            setFields.add(LessonConstants.DB_LESSON_NAME_COLUMN + " = " + Utils.wrapWithSingleQuotes(lesson.getName()));
        }

        if (setFields.size() != 0) {
            sb.append(String.join(",", setFields));
            sb.append(" WHERE ").append(LessonConstants.DB_LESSON_ID_COLUMN).append(" = ").append(lesson.getLessonId());
            DBResult dbResult = update(sb.toString());
            if (!dbResult.isSuccess()) {
                String msg = Utils.getMsg("Error updating lesson details with lessonId '{}' ", lesson.getLessonId());
                LOGGER.error(msg, dbResult.getCause());
                throw new RuntimeException(msg, dbResult.getCause());
            }
            LOGGER.info(" Successfully updated details of lesson with id '{}' ", lesson.getLessonId());
        }
    }


    @Override
    public Lesson get(Long id) {
        Lesson lesson = null;
        DBResult dbResult = DBQueryUtil.getEntityByEntityId(LessonConstants.DB_LESSON_TABLE_NAME, LessonConstants.DB_LESSON_ID_COLUMN, id);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error fetching lesson with lessonId '{}' ", id);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Lesson> lessons = getLessons((JSONArray) dbResult.getResult());
        if (lessons.size() > 0) {
            lesson = lessons.get(0);
        }
        return lesson;
    }

    @Override
    public List<Lesson> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(LessonConstants.DB_LESSON_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error fetching lesson details from lessons table");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        return getLessons((JSONArray) dbResult.getResult());

    }

    @Override
    public void addLessonToCourse(Long lessonId, Long courseId) {
        JSONArray params = new JSONArray();
        params.put(0, lessonId);
        params.put(1, courseId);
        DBResult dbResult = DBQueryUtil.createEntity(DB_LESSON_COURSES_TABLE_NAME, Arrays.asList(LessonConstants.DB_LESSON_ID_COLUMN, CourseConstants.DB_COURSE_ID_COLUMN), params, Constants.DB_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error adding lesson '{}' to course: '{}'  ", lessonId, courseId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully added lesson '{}' to course '{}' ", lessonId, courseId);

    }


    @Override
    public void updateLessonActiveStatus(Long lessonId, Long courseId, boolean isActive) {
        String updateQuery = new StringBuilder("UPDATE ").append(DB_LESSON_COURSES_TABLE_NAME)
                .append(" SET ").append(Constants.DB_IS_ACTIVE_COLUMN).append(" = ").append(isActive)
                .append(" WHERE ").append(CourseConstants.DB_COURSE_ID_COLUMN).append(" = ").append(courseId)
                .append(" AND ").append(LessonConstants.DB_LESSON_ID_COLUMN).append(" = ").append(lessonId).toString();
        DBResult dbResult = update(updateQuery);
        if (!dbResult.isSuccess()) {
            String msg = getMsg("Failed to update the status of lesson '{}' for course '{}' to '{}' ", lessonId, courseId, isActive);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info(getMsg("Successfully update the status of lessons '{}' fpr course '{}' to '{}' ", lessonId, courseId, isActive));

    }

    @Override
    public List<Lesson> getActiveLessons(Long courseId) {
        String inQuery = new StringBuilder("SELECT ").append(LessonConstants.DB_LESSON_ID_COLUMN).append(" FROM ")
                .append(DB_LESSON_COURSES_TABLE_NAME).append(" WHERE ")
                .append(CourseConstants.DB_COURSE_ID_COLUMN).append(" = ").append(courseId)
                .append(" AND ")
                .append(Constants.DB_IS_ACTIVE_COLUMN).append(" = ").append(true).toString();

        String query = new StringBuilder("SELECT * FROM ").append(LessonConstants.DB_LESSON_TABLE_NAME).append(" WHERE ")
                .append(LessonConstants.DB_LESSON_ID_COLUMN).append(" IN (").append(inQuery).append(" )").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all active lessons for course: {}", courseId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getLessons(result);
    }

    private List<Lesson> getLessons(JSONArray result) {
        List<Lesson> lessons = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                Lesson lesson = new Lesson();
                lesson.setLessonId(jsonObject.getLong(LessonConstants.DB_LESSON_ID_COLUMN));
                lesson.setName(jsonObject.getString(LessonConstants.DB_LESSON_NAME_COLUMN));
                lesson.setUserId(jsonObject.getLong(LessonConstants.DB_LESSON_ID_COLUMN));
                lessons.add(lesson);
            }
        }
        return lessons;
    }
}


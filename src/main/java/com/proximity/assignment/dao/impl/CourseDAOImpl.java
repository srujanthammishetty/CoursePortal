package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.Constants.CourseConstants;
import com.proximity.assignment.commons.Constants.UserConstants;
import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.CourseDAO;
import com.proximity.assignment.model.Course;
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

import static com.proximity.assignment.util.Utils.wrapWithRoundBracket;

/**
 * @author sthammishetty on 18/06/21
 */

@Repository
public class CourseDAOImpl extends AbstractDAOImpl<Course> implements CourseDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseDAOImpl.class);

    public CourseDAOImpl() {

    }

    @Override
    public Long createCourse(String courseName, Long userId) {
        JSONArray params = new JSONArray();
        params.put(0, courseName);
        params.put(1, userId);
        DBResult dbResult = DBQueryUtil.createEntity(CourseConstants.DB_COURSES_TABLE_NAME, Arrays.asList(CourseConstants.DB_COURSE_NAME_COLUMN, UserConstants.DB_USER_ID_COLUMN), params, CourseConstants.DB_COURSE_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating course '{}' for instructor with userId '{}'  ", courseName, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created course '{}' for instructor with userId '{}' ", courseName, userId);
        return DBQueryUtil.getIdFromResult(dbResult, CourseConstants.DB_COURSE_ID_COLUMN);
    }

    @Override
    public void deleteCourse(Long courseId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(CourseConstants.DB_COURSES_TABLE_NAME).append(" WHERE ")
                .append(CourseConstants.DB_COURSE_ID_COLUMN).append(" = ").append(courseId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete course {}", courseId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully deleted course with courseId '{}' ", courseId);
    }

    @Override
    public void updateCourse(Course course) {
        StringBuilder sb = new StringBuilder(" UPDATE ").append(CourseConstants.DB_COURSES_TABLE_NAME).append(" SET ");

        List<String> setFields = new ArrayList<>();
        if (course.getName() != null) {
            setFields.add(CourseConstants.DB_COURSE_NAME_COLUMN + " = " + Utils.wrapWithSingleQuotes(course.getName()));
        }

        if (course.getActive() != null) {
            setFields.add(Constants.DB_IS_ACTIVE_COLUMN + " = " + course.getActive());
        }

        if (setFields.size() != 0) {
            sb.append(String.join(",", setFields));
            sb.append(" WHERE ").append(CourseConstants.DB_COURSE_ID_COLUMN).append(" = ").append(course.getCourseId());
            DBResult dbResult = update(sb.toString());
            if (!dbResult.isSuccess()) {
                String msg = Utils.getMsg("Error updating course details of course '{}' ", course.getCourseId());
                LOGGER.error(msg, dbResult.getCause());
                throw new RuntimeException(msg, dbResult.getCause());
            }
            LOGGER.info(" Successfully updated course details of course '{}', '{}' ", course.getCourseId(), course);
        }
    }

    @Override
    public Course get(Long courseId) {
        Course course = null;
        DBResult dbResult = DBQueryUtil.getEntityByEntityId(CourseConstants.DB_COURSES_TABLE_NAME, CourseConstants.DB_COURSE_ID_COLUMN, courseId);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting course details for course '{}' ", courseId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        List<Course> courses = getCourses(result);
        if (courses.size() > 0) {
            course = courses.get(0);
        }
        return course;
    }

    @Override
    public List<Course> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(CourseConstants.DB_COURSES_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all courses");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getCourses(result);
    }


    @Override
    public void subscribeToCourse(Long userId, Long courseId) {
        String insertQuery = new StringBuilder("INSERT INTO ").append(Constants.DB_STUDENT_COURSES_TABLE_NAME)
                .append(" %1$s ").append(" VALUES ( ?, ?) ON CONFLICT %1$s DO NOTHING ").toString();
        String columnNames = wrapWithRoundBracket(String.join(",", Arrays.asList(UserConstants.DB_USER_ID_COLUMN, CourseConstants.DB_COURSE_ID_COLUMN)));
        insertQuery = String.format(insertQuery, columnNames);
        JSONArray params = new JSONArray();
        params.put(0, userId);
        params.put(1, courseId);
        DBResult dbResult = updateWithParams(insertQuery, params);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error subscribing to course '{}' for user '{}' ", courseId, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully subscribed course '{}' for user '{}' ", courseId, userId);
    }

    @Override
    public List<Course> getSubscribedCourses(Long userId) {
        String inQuery = new StringBuilder(" SELECT ").append(CourseConstants.DB_COURSE_ID_COLUMN).append(" FROM ")
                .append(Constants.DB_STUDENT_COURSES_TABLE_NAME).append(" WHERE ").append(UserConstants.DB_USER_ID_COLUMN)
                .append(" = ").append(userId).toString();

        String query = new StringBuilder(" SELECT * FROM ").append(CourseConstants.DB_COURSES_TABLE_NAME)
                .append(" WHERE ").append(CourseConstants.DB_COURSE_ID_COLUMN).append(" IN ")
                .append(Utils.wrapWithRoundBracket(inQuery)).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting subscribed courses list for user with userId '{}' ", userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());

        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getCourses(result);
    }

    @Override
    public void unsubscribeToCourse(Long userId, Long courseId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(Constants.DB_STUDENT_COURSES_TABLE_NAME).append(" WHERE ")
                .append(UserConstants.DB_USER_ID_COLUMN).append(" = ").append(userId).append(" AND ")
                .append(CourseConstants.DB_COURSE_ID_COLUMN).append(" = ").append(courseId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to unsubsribed course '{}' for user '{}' ", courseId, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully unsubscribed course '{}' for user '{}' ", courseId, userId);
    }

    @Override
    public List<Course> getAllActiveCourses() {
        String query = new StringBuilder("SELECT * FROM ").append(CourseConstants.DB_COURSES_TABLE_NAME).append(" WHERE ")
                .append(Constants.DB_IS_ACTIVE_COLUMN).append(" = true").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all active courses");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getCourses(result);
    }

    @Override
    public List<Course> getCourses(Long subjectId) {
        String inQuery = new StringBuilder("SELECT ").append(CourseConstants.DB_COURSE_ID_COLUMN)
                .append(" FROM ")
                .append(Constants.DB_COURSE_SUBJECT_TABLE_NAME).append(" WHERE ")
                .append(Constants.SubjectConstants.DB_SUBJECT_ID_COLUMN).append(" = ").append(subjectId).toString();
        String query = new StringBuilder(" SELECT * FROM ").append(CourseConstants.DB_COURSES_TABLE_NAME).append(" WHERE ")
                .append(CourseConstants.DB_COURSE_ID_COLUMN).append(" IN ").append(" ( ").append(inQuery).append(" )").toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting courses that contains subject : {}", subjectId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getCourses(result);
    }


    private List<Course> getCourses(JSONArray result) {
        List<Course> courses = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                Course course = new Course();
                course.setCourseId(jsonObject.getLong(CourseConstants.DB_COURSE_ID_COLUMN));
                course.setUserId(jsonObject.getLong(UserConstants.DB_USER_ID_COLUMN));
                course.setName(jsonObject.getString(CourseConstants.DB_COURSE_NAME_COLUMN));
                course.setActive(jsonObject.getBoolean(Constants.DB_IS_ACTIVE_COLUMN));
                courses.add(course);
            }
        }
        return courses;
    }

}
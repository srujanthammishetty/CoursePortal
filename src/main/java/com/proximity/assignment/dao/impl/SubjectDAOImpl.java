package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.Constants.SubjectConstants;
import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.SubjectDAO;
import com.proximity.assignment.model.Subject;
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

import static com.proximity.assignment.commons.Constants.DB_COURSE_SUBJECT_TABLE_NAME;

/**
 * @author sthammishetty on 19/06/21
 */
@Repository
public class SubjectDAOImpl extends AbstractDAOImpl<Subject> implements SubjectDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectDAOImpl.class);

    public SubjectDAOImpl() {
    }

    @Override
    public void createSubject(String name, Long userId) {
        JSONArray params = new JSONArray();
        params.put(0, name);
        params.put(1, userId);
        DBResult dbResult = DBQueryUtil.createEntity(SubjectConstants.DB_SUBJECTS_TABLE_NAME, Arrays.asList(SubjectConstants.DB_SUBJECT_NAME_COLUMN, Constants.UserConstants.DB_USER_ID_COLUMN), params);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating subject '{}', userId '{}'  ", name, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created subject '{}' by userId : {}", name, userId);
    }

    @Override
    public void deleteSubject(Long subjectId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(SubjectConstants.DB_SUBJECTS_TABLE_NAME).append(" WHERE ")
                .append(SubjectConstants.DB_SUBJECT_ID_COLUMN).append(" = ").append(subjectId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete subject: {}", subjectId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully deleted subject {}", subjectId);
    }

    @Override
    public Subject get(Long id) {
        Subject subject = null;
        DBResult dbResult = DBQueryUtil.getEntityById(SubjectConstants.DB_SUBJECTS_TABLE_NAME, SubjectConstants.DB_SUBJECT_ID_COLUMN, id);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting details of subject : {}", id);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        List<Subject> subjects = getSubjects(result);
        if (subjects.size() > 0) {
            subject = subjects.get(0);
        }
        return subject;
    }

    @Override
    public List<Subject> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(SubjectConstants.DB_SUBJECTS_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all subjects");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getSubjects(result);

    }

    @Override
    public void addSubjectToCourse(Long courseId, Long subjectId) {
        JSONArray params = new JSONArray();
        params.put(0, courseId);
        params.put(1, subjectId);
        DBResult dbResult = DBQueryUtil.createEntity(DB_COURSE_SUBJECT_TABLE_NAME, Arrays.asList(Constants.CourseConstants.DB_COURSE_ID_COLUMN, SubjectConstants.DB_SUBJECT_ID_COLUMN), params);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error adding subject '{}' to course: {}  ", subjectId, courseId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully added subject: {} to course: {}", subjectId, courseId);
    }

    @Override
    public void updateSubject(Subject subject) {
        StringBuilder sb = new StringBuilder(" UPDATE ").append(SubjectConstants.DB_SUBJECTS_TABLE_NAME).append(" SET ");

        List<String> setFields = new ArrayList<>();
        if (subject.getName() != null) {
            setFields.add(SubjectConstants.DB_SUBJECT_NAME_COLUMN + " = " + Utils.wrapWithSingleQuotes(subject.getName()));
        }

        if (setFields.size() != 0) {
            sb.append(String.join(",", setFields));
            sb.append(" WHERE ").append(SubjectConstants.DB_SUBJECT_ID_COLUMN).append(" = ").append(subject.getSubjectId());
            DBResult dbResult = update(sb.toString());
            if (!dbResult.isSuccess()) {
                String msg = Utils.getMsg("Error updating subject details of subject '{}'  ", subject.getSubjectId());
                LOGGER.error(msg, dbResult.getCause());
                throw new RuntimeException(msg, dbResult.getCause());
            }
            LOGGER.info(" Successfully updated subject details of subject '{}' ", subject.getSubjectId());
        }
    }


    private List<Subject> getSubjects(JSONArray result) {
        List<Subject> subjects = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                Subject subject = new Subject();
                subject.setName(jsonObject.getString(SubjectConstants.DB_SUBJECT_NAME_COLUMN));
                subject.setSubjectId(jsonObject.getLong(SubjectConstants.DB_SUBJECT_ID_COLUMN));
                subject.setUserId(jsonObject.getLong(Constants.UserConstants.DB_USER_ID_COLUMN));
                subjects.add(subject);
            }
        }
        return subjects;
    }


}

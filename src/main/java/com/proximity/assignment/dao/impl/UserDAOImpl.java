package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.User;
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

import static com.proximity.assignment.commons.Constants.UserConstants;

/**
 * @author sthammishetty on 18/06/21
 */
@Repository
public class UserDAOImpl extends AbstractDAOImpl<User> implements UserDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

    public UserDAOImpl() {
    }

    @Override
    public void createUser(String name, String email, boolean isInstructor) {
        JSONArray params = new JSONArray();
        params.put(0, name);
        params.put(1, email);
        params.put(2, isInstructor);
        DBResult dbResult = DBQueryUtil.createEntity(UserConstants.DB_USERS_TABLE_NAME, Arrays.asList(UserConstants.DB_USER_NAME_COLUMN, UserConstants.DB_USER_EMAIL_ID_COLUMN, UserConstants.DB_IS_INSTRUCTOR_COLUMN), params);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating user '{}', isInstructor: {}  ", name, isInstructor);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created user '{}', isInstructor ' {}' ", name, isInstructor);

    }

    @Override
    public void deleteUser(String emailId) {
        String query = new StringBuilder("DELETE FROM ").append(UserConstants.DB_USERS_TABLE_NAME)
                .append(" WHERE ").append(UserConstants.EMAIL_ID).append(" = ")
                .append(Utils.wrapWithSingleQuotes(emailId)).toString();
        DBResult dbResult = update(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete user with emailId  {}", emailId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Deleted user with emailId : {}", emailId);
    }

    @Override
    public User get(Long id) {
        User user = null;
        String query = new StringBuilder("SELECT * FROM ").append(UserConstants.DB_USERS_TABLE_NAME)
                .append(" WHERE ").append(UserConstants.DB_USER_ID_COLUMN).append(" = ").append(id).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error fetching user with userId : '{}' ", id);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<User> users = getUsers((JSONArray) dbResult.getResult());
        if (users.size() > 0) {
            user = users.get(0);
        }
        return user;
    }

    @Override
    public List<User> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(UserConstants.DB_USERS_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error fetching users from users table");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        return getUsers((JSONArray) dbResult.getResult());

    }


    private List<User> getUsers(JSONArray result) {
        List<User> users = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                User user = new User();
                user.setInstructor(jsonObject.getBoolean(UserConstants.DB_IS_INSTRUCTOR_COLUMN));
                user.setName(jsonObject.getString(UserConstants.DB_USER_NAME_COLUMN));
                user.setUserId(jsonObject.getLong(UserConstants.DB_USER_ID_COLUMN));
                users.add(user);
            }
        }
        return users;
    }
}

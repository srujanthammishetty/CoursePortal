package com.proximity.assignment.dao;

import com.proximity.assignment.model.User;

/**
 * @author sthammishetty on 18/06/21
 */
public interface UserDAO extends AbstractDAO<User>{

    void createUser(String name, String email, boolean isInstructor);

    void deleteUser(String emailId);
}

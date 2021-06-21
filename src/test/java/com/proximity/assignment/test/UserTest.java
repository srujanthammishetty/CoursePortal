package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.User;
import com.proximity.assignment.util.DBServiceUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author sthammishetty on 21/06/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@TestPropertySource(locations = "classpath:deployment.properties")
public class UserTest {

    private static final String USER_EMAIL_ID = "test.email2@test.com";
    private static final String USER_NAME = "test_user";


    @BeforeClass
    public static void initialize() {
        PropertiesService.loadPropertiesFromFileSystem();
        DBServiceUtil.initializeHikariDataSource();
    }

    @Autowired
    UserDAO userDAO;

    @Test
    public void createUser() {
        userDAO.createUser(USER_NAME, USER_EMAIL_ID, false);
        List<User> users = userDAO.listAll();
        boolean hasUser = false;
        for (User user : users) {
            if (USER_NAME.equals(user.getName()) && USER_EMAIL_ID.equals(user.getEmailId())) {
                hasUser = true;
                break;
            }
        }
        Assert.assertEquals(true, hasUser);
    }

    @Test
    public void listAllUsers() {
        List<User> users = userDAO.listAll();
        Assert.assertEquals(1, users.size());
    }


    @Test
    public void deleteUser() {
        userDAO.deleteUser(USER_EMAIL_ID);
        List<User> users = userDAO.listAll();
        Assert.assertEquals(0, users.size());

    }

}

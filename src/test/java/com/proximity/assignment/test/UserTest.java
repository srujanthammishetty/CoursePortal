package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.User;
import com.proximity.assignment.test.config.TestConfiguration;
import com.proximity.assignment.util.DBServiceUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

/**
 * @author sthammishetty on 21/06/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@TestPropertySource(locations = "classpath:deployment.properties")
public class UserTest {

    private Long userId = null;

    @Autowired
    UserDAO userDAO;

    @BeforeClass
    public static void initialize() {
        PropertiesService.loadPropertiesFromFileSystem();
        DBServiceUtil.initializeHikariDataSource();
    }

    @Before
    public void setUp() {
        String randomString = UUID.randomUUID().toString();
        String userEmail = randomString + "@test.com";
        String userName = randomString + "_username";
        userId = userDAO.createUser(userName, userEmail, false);
    }

    @After
    public void cleanup() {
        userDAO.deleteUserById(userId);
    }

    @Test
    public void createUser() {
        List<User> users = userDAO.listAll();
        boolean hasUser = false;
        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                hasUser = true;
                break;
            }
        }
        Assert.assertEquals(true, hasUser);
    }

    @Test
    public void listAllUsers() {
        List<User> users = userDAO.listAll();
        boolean isUserExist = false;
        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                isUserExist = true;
                break;
            }
        }
        Assert.assertEquals(true, isUserExist);
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void deleteUser() {
        userDAO.deleteUserById(userId);
        List<User> users = userDAO.listAll();
        boolean isUserExist = false;
        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                isUserExist = true;
                break;
            }
        }
        Assert.assertEquals(false, isUserExist);
    }
}

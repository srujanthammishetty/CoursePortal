package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.CourseDAO;
import com.proximity.assignment.dao.SubjectDAO;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.Course;
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
 * @author sthammishetty on 22/06/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@TestPropertySource(locations = "classpath:deployment.properties")
public class CourseTest {

    private Long userId = null;
    private Long courseId = null;
    private Long subjectId = null;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private SubjectDAO subjectDAO;

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
        userId = userDAO.createUser(userName, userEmail, true);

        String courseName = UUID.randomUUID().toString() + "_course";
        courseId = courseDAO.createCourse(courseName, userId);

        String subjectName = UUID.randomUUID().toString() + "_subject";
        subjectId = subjectDAO.createSubject(subjectName, userId);
    }

    @After
    public void cleanup() {
        courseDAO.deleteCourse(courseId);
        userDAO.deleteUserById(userId);
        subjectDAO.deleteSubject(subjectId);
    }

    @Test
    public void createCourse() {
        List<Course> courses = courseDAO.listAll();
        boolean hasCourse = false;
        for (Course course : courses) {
            if (courseId.equals(course.getCourseId())) {
                hasCourse = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCourse);
    }

    @Test
    public void updateCourse() {
        String name = "updatedCourseName";
        Course course = new Course();
        course.setCourseId(courseId);
        course.setName(name);
        courseDAO.updateCourse(course);
        course = courseDAO.get(courseId);
        Assert.assertEquals(name, course.getName());
    }

    @Test
    public void deleteCourse() {
        courseDAO.deleteCourse(courseId);
        Course course = courseDAO.get(courseId);
        Assert.assertEquals(null, course);
    }

    @Test
    public void getCourse() {
        Course course = courseDAO.get(courseId);
        Assert.assertNotNull(course);
        Assert.assertEquals(courseId, course.getCourseId());
    }

    @Test
    public void listAllCourse() {
        List<Course> courses = courseDAO.listAll();
        boolean hasCourse = false;
        for (Course course : courses) {
            if (courseId.equals(course.getCourseId())) {
                hasCourse = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCourse);
        Assert.assertEquals(1, courses.size());
    }

    @Test
    public void subscribeToCourse() {
        courseDAO.subscribeToCourse(userId, courseId);
        List<Course> subscribedCourses = courseDAO.getSubscribedCourses(userId);
        Assert.assertEquals(1, subscribedCourses.size());
        boolean hasSubscribedCourse = false;
        for (Course course : subscribedCourses) {
            if (courseId.equals(course.getCourseId())) {
                hasSubscribedCourse = true;
                break;
            }
        }
        Assert.assertEquals(true, hasSubscribedCourse);
    }

    @Test
    public void unSubscribeToCourse() {
        subscribeToCourse();
        courseDAO.unsubscribeToCourse(userId, courseId);
        List<Course> subscribedCourses = courseDAO.getSubscribedCourses(userId);
        Assert.assertEquals(0, subscribedCourses.size());
    }

    @Test
    public void getActiveCourses() {
        Course course = courseDAO.get(courseId);
        course.setActive(true);
        courseDAO.updateCourse(course);
        List<Course> allActiveCourses = courseDAO.getAllActiveCourses();
        Assert.assertEquals(1, allActiveCourses.size());
    }

    @Test
    public void getCoursesWithSubject() {
        subjectDAO.addSubjectToCourse(courseId, subjectId);
        List<Course> courses = courseDAO.getCourses(subjectId);
        Assert.assertEquals(1, courses.size());
        boolean hasCourse = false;
        for (Course course : courses) {
            if (courseId.equals(course.getCourseId())) {
                hasCourse = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCourse);
    }
}

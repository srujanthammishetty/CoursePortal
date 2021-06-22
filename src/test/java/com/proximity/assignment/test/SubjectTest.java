package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.CourseDAO;
import com.proximity.assignment.dao.SubjectDAO;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.Course;
import com.proximity.assignment.model.Subject;
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
public class SubjectTest {

    private Long subjectId = null;
    private Long userId = null;
    private Long courseId = null;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    SubjectDAO subjectDAO;

    @Autowired
    CourseDAO courseDAO;

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

        String subjectName = UUID.randomUUID().toString() + "_subject";
        subjectId = subjectDAO.createSubject(subjectName, userId);

        String courseName = UUID.randomUUID().toString() + "_course";
        courseId = courseDAO.createCourse(courseName, userId);
    }

    @After
    public void cleanup() {
        subjectDAO.deleteSubject(subjectId);
        userDAO.deleteUserById(userId);
        courseDAO.deleteCourse(courseId);
    }

    @Test
    public void createSubject() {
        boolean hasSubject = false;
        List<Subject> subjects = subjectDAO.listAll();
        for (Subject subject : subjects) {
            if (subjectId.equals(subject.getSubjectId())) {
                hasSubject = true;
                break;
            }
        }
        Assert.assertEquals(true, hasSubject);
    }

    @Test
    public void updateSubject() {
        String name = "updatedSubject";
        Subject subject = new Subject();
        subject.setSubjectId(subjectId);
        subject.setName(name);
        subjectDAO.updateSubject(subject);
        subject = subjectDAO.get(subjectId);
        Assert.assertEquals(name, subject.getName());
    }

    @Test
    public void deleteSubject() {
        subjectDAO.deleteSubject(subjectId);
        Subject subject = subjectDAO.get(subjectId);
        Assert.assertNull(subject);
    }

    @Test
    public void getSubject() {
        Subject subject = subjectDAO.get(subjectId);
        Assert.assertNotNull(subject);
        Assert.assertEquals(subjectId, subject.getSubjectId());
    }

    @Test
    public void listAll() {
        List<Subject> subjects = subjectDAO.listAll();
        Assert.assertEquals(1, subjects.size());
        boolean hasSubject = false;
        for (Subject subject : subjects) {
            if (subjectId.equals(subject.getSubjectId())) {
                hasSubject = true;
                break;
            }
        }
        Assert.assertEquals(true, hasSubject);

    }

    @Test
    public void addSubjectToCourse() {
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

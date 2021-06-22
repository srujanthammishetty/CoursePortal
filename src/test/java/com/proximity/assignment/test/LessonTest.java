package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.CourseDAO;
import com.proximity.assignment.dao.LessonDAO;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.Lesson;
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
public class LessonTest {

    private Long userId = null;
    private Long courseId = null;
    private Long lessonId = null;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    LessonDAO lessonDAO;

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

        String lessonName = UUID.randomUUID().toString() + "_lesson";
        lessonId = lessonDAO.createLesson(lessonName, userId);

        String courseName = UUID.randomUUID().toString() + "_course";
        courseId = courseDAO.createCourse(courseName, userId);
    }

    @After
    public void cleanup() {
        lessonDAO.deleteLesson(lessonId);
        userDAO.deleteUserById(userId);
        courseDAO.deleteCourse(courseId);
    }

    @Test
    public void createLesson() {
        boolean hasLesson = false;
        List<Lesson> lessons = lessonDAO.listAll();
        for (Lesson lesson : lessons) {
            if (lessonId.equals(lesson.getLessonId())) {
                hasLesson = true;
                break;
            }
        }
        Assert.assertEquals(true, hasLesson);
    }

    @Test
    public void updateLesson() {
        String name = "updatedLessonName";
        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setName(name);
        lessonDAO.updateLesson(lesson);
        lesson = lessonDAO.get(lessonId);
        Assert.assertEquals(name, lesson.getName());
    }

    @Test
    public void deleteLesson() {
        lessonDAO.deleteLesson(lessonId);
        Lesson lesson = lessonDAO.get(lessonId);
        Assert.assertNull(lesson);
    }

    @Test
    public void getLesson() {
        Lesson lesson = lessonDAO.get(lessonId);
        Assert.assertNotNull(lesson);
    }

    @Test
    public void listAll() {
        List<Lesson> lessons = lessonDAO.listAll();
        Assert.assertEquals(1, lessons.size());
        boolean hasLessonId = false;
        for (Lesson lesson : lessons) {
            if (lessonId.equals(lesson.getLessonId())) {
                hasLessonId = true;
                break;
            }
        }
        Assert.assertEquals(true, hasLessonId);
    }

    @Test
    public void addLessonToCourse() {
        lessonDAO.addLessonToCourse(lessonId, courseId);
        List<Lesson> activeLessons = lessonDAO.getActiveLessons(courseId);
        Assert.assertEquals(activeLessons.size(), 1);
        boolean hasLessonInCourse = false;
        for (Lesson lesson : activeLessons) {
            if (lessonId.equals(lesson.getLessonId())) {
                hasLessonInCourse = true;
                break;
            }
        }
        Assert.assertEquals(true, hasLessonInCourse);
    }

    @Test
    public void activeLessonsInGivenCourseId() {
        addLessonToCourse();
        lessonDAO.updateLessonActiveStatus(lessonId, courseId, false);
        List<Lesson> activeLessons = lessonDAO.getActiveLessons(courseId);
        Assert.assertEquals(0, activeLessons.size());
    }


    @Test
    public void updateLessonActiveStatusInGivenCourse() {
        addLessonToCourse();
        lessonDAO.updateLessonActiveStatus(lessonId, courseId, false);
        List<Lesson> activeLessons = lessonDAO.getActiveLessons(courseId);
        Assert.assertEquals(0, activeLessons.size());
    }
}

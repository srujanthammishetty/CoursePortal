package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.LessonDAO;
import com.proximity.assignment.dao.TagDAO;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.dao.VideoDAO;
import com.proximity.assignment.model.Video;
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
public class VideoTest {

    private Long videoId = null;
    private Long userId = null;
    private Long lessonId = null;
    private Long tagId = null;
    private String videoTitle;
    private String tagTitle;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    VideoDAO videoDAO;

    @Autowired
    LessonDAO lessonDAO;

    @Autowired
    TagDAO tagDAO;

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

        videoTitle = UUID.randomUUID().toString() + "_videoTitle";
        String videoLink = UUID.randomUUID().toString() + "_videoLink";
        videoId = videoDAO.createVideo(videoTitle, videoLink, userId);

        tagTitle = UUID.randomUUID().toString() + "_tagTitle";
        tagId = tagDAO.createTag(tagTitle, userId);

        String lessonName = UUID.randomUUID().toString() + "_lessonName";
        lessonId = lessonDAO.createLesson(lessonName, userId);
    }

    @After
    public void cleanup() {
        videoDAO.deleteVideo(videoId);
        userDAO.deleteUserById(userId);
        tagDAO.deleteTag(tagId);
        lessonDAO.deleteLesson(lessonId);
    }

    @Test
    public void createVideo() {
        boolean hasVideo = false;
        List<Video> videos = videoDAO.listAll();
        for (Video video : videos) {
            if (videoId.equals(video.getVideoId())) {
                hasVideo = true;
                break;
            }
        }
        Assert.assertEquals(true, hasVideo);
    }

    @Test
    public void updateVideo() {
        String title = "updatedVideoTitle";
        Video video = new Video();
        video.setVideoId(videoId);
        video.setTitle(title);
        videoDAO.updateVideo(video);
        video = videoDAO.get(videoId);
        Assert.assertEquals(title, video.getTitle());
    }

    @Test
    public void deleteVideo() {
        videoDAO.deleteVideo(videoId);
        Video video = videoDAO.get(videoId);
        Assert.assertNull(video);
    }

    @Test
    public void getVideo() {
        Video video = videoDAO.get(videoId);
        Assert.assertNotNull(video);
    }

    @Test
    public void listAll() {
        List<Video> videos = videoDAO.listAll();
        Assert.assertEquals(1, videos.size());
        boolean hasVideoId = false;
        for (Video video : videos) {
            if (videoId.equals(video.getVideoId())) {
                hasVideoId = true;
                break;
            }
        }
        Assert.assertEquals(true, hasVideoId);
    }

    @Test
    public void addVideoToLesson() {
        videoDAO.addVideoToLesson(videoId, lessonId);
        boolean isVideoAttachedToLesson = false;
        List<Video> allActiveVideosByLesson = videoDAO.getAllActiveVideosByLesson(lessonId);
        for (Video video : allActiveVideosByLesson) {
            if (videoId.equals(video.getVideoId())) {
                isVideoAttachedToLesson = true;
                break;
            }
        }
        Assert.assertEquals(true, isVideoAttachedToLesson);
    }

    @Test
    public void addTagToVideo() {
        videoDAO.addTagToVideo(videoId, tagId);
        Video video = videoDAO.getVideoByTagId(tagId);
        Assert.assertNotNull(video);
        Assert.assertEquals(videoId, video.getVideoId());
    }

    @Test
    public void videoByTitle() {
        Video video = videoDAO.getVideoByTitle(UUID.randomUUID().toString());
        Assert.assertNull(video);
        video = videoDAO.getVideoByTitle(videoTitle);
        Assert.assertNotNull(video);
        Assert.assertEquals(videoId, video.getVideoId());
    }

    @Test
    public void videoByTag() {
        addTagToVideo();
        Video video = videoDAO.getVideoByTagName(UUID.randomUUID().toString());
        Assert.assertNull(video);
        video = videoDAO.getVideoByTagName(tagTitle);
        Assert.assertNotNull(video);
        Assert.assertEquals(videoId, video.getVideoId());
    }
}

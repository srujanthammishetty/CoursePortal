package com.proximity.assignment.test;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.dao.TagDAO;
import com.proximity.assignment.dao.UserDAO;
import com.proximity.assignment.model.Tag;
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
public class TagTest {

    private Long tagId = null;
    private Long userId = null;

    @Autowired
    private UserDAO userDAO;

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

        String tagTitle = UUID.randomUUID().toString() + "_tag";
        tagId = tagDAO.createTag(tagTitle, userId);
    }

    @After
    public void cleanup() {
        tagDAO.deleteTag(tagId);
        userDAO.deleteUserById(userId);
    }

    @Test
    public void createTag() {
        boolean hasTag = false;
        List<Tag> tags = tagDAO.listAll();
        for (Tag tag : tags) {
            if (tagId.equals(tag.getTagId())) {
                hasTag = true;
                break;
            }
        }
        Assert.assertEquals(true, hasTag);
    }

    @Test
    public void updateTag() {
        String title = "updatedTagTitle";
        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setTitle(title);
        tagDAO.updateTag(tag);
        tag = tagDAO.get(tagId);
        Assert.assertEquals(title, tag.getTitle());
    }

    @Test
    public void deleteTag() {
        tagDAO.deleteTag(tagId);
        Tag tag = tagDAO.get(tagId);
        Assert.assertNull(tag);
    }

    @Test
    public void getTag() {
        Tag tag = tagDAO.get(tagId);
        Assert.assertNotNull(tag);
    }

    @Test
    public void listAll() {
        List<Tag> tags = tagDAO.listAll();
        Assert.assertEquals(1, tags.size());
        boolean hasTagId = false;
        for (Tag tag : tags) {
            if (tagId.equals(tag.getTagId())) {
                hasTagId = true;
                break;
            }
        }
        Assert.assertEquals(true, hasTagId);
    }
}

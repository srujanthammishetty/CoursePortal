package com.proximity.assignment.dao.impl;

import com.proximity.assignment.commons.Constants.TagConstants;
import com.proximity.assignment.commons.Constants.UserConstants;
import com.proximity.assignment.commons.DBResult;
import com.proximity.assignment.dao.TagDAO;
import com.proximity.assignment.model.Tag;
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

/**
 * @author sthammishetty on 19/06/21
 */
@Repository
public class TagDAOImpl extends AbstractDAOImpl<Tag> implements TagDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagDAOImpl.class);

    public TagDAOImpl() {
    }

    @Override
    public Long createTag(String title, Long userId) {
        JSONArray params = new JSONArray();
        params.put(0, title);
        params.put(1, userId);
        DBResult dbResult = DBQueryUtil.createEntity(TagConstants.DB_TAGS_TABLE_NAME, Arrays.asList(TagConstants.DB_TAG_TITLE_COLUMN, UserConstants.DB_USER_ID_COLUMN), params, TagConstants.DB_TAG_ID_COLUMN);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error creating tag '{}', userId '{}'  ", title, userId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully created tag '{}' with userId : {}", title, userId);
        return DBQueryUtil.getIdFromResult(dbResult, TagConstants.DB_TAG_ID_COLUMN);
    }

    @Override
    public void deleteTag(Long tagId) {
        String deleteQuery = new StringBuilder("DELETE FROM ").append(TagConstants.DB_TAGS_TABLE_NAME).append(" WHERE ")
                .append(TagConstants.DB_TAG_ID_COLUMN).append(" = ").append(tagId).toString();
        DBResult dbResult = update(deleteQuery);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Failed to delete tag: {}", tagId);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        LOGGER.info("Successfully deleted tag with tagId {}", tagId);
    }

    @Override
    public void updateTag(Tag tag) {
        StringBuilder sb = new StringBuilder(" UPDATE ").append(TagConstants.DB_TAGS_TABLE_NAME).append(" SET ");

        List<String> setFields = new ArrayList<>();
        if (tag.getTitle() != null) {
            setFields.add(TagConstants.DB_TAG_TITLE_COLUMN + " = " + Utils.wrapWithSingleQuotes(tag.getTitle()));
        }
        if (setFields.size() != 0) {
            sb.append(String.join(",", setFields));
            sb.append(" WHERE ").append(TagConstants.DB_TAG_ID_COLUMN).append(" = ").append(tag.getTagId());
            DBResult dbResult = update(sb.toString());
            if (!dbResult.isSuccess()) {
                String msg = Utils.getMsg("Error updating tag details of tag : {} ", tag.getTagId());
                LOGGER.error(msg, dbResult.getCause());
                throw new RuntimeException(msg, dbResult.getCause());
            }
            LOGGER.info(" Successfully updated tag details of tag '{}' ", tag.getTagId());
        }
    }

    @Override
    public Tag get(Long id) {
        Tag tag = null;
        DBResult dbResult = DBQueryUtil.getEntityByEntityId(TagConstants.DB_TAGS_TABLE_NAME, TagConstants.DB_TAG_ID_COLUMN, id);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting details of tag : {}", id);
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        List<Tag> tags = getTags((JSONArray) dbResult.getResult());
        if (tags.size() > 0) {
            tag = tags.get(0);
        }
        return tag;
    }

    @Override
    public List<Tag> listAll() {
        String query = new StringBuilder("SELECT * FROM ").append(TagConstants.DB_TAGS_TABLE_NAME).toString();
        DBResult dbResult = executeQuery(query);
        if (!dbResult.isSuccess()) {
            String msg = Utils.getMsg("Error getting all tags");
            LOGGER.error(msg, dbResult.getCause());
            throw new RuntimeException(msg, dbResult.getCause());
        }
        JSONArray result = (JSONArray) dbResult.getResult();
        return getTags(result);
    }


    private List<Tag> getTags(JSONArray result) {
        List<Tag> tags = new ArrayList<>();
        if (result != null && result.length() > 0) {
            JSONObject jsonObject;
            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                Tag tag = new Tag();
                tag.setTagId(jsonObject.getLong(TagConstants.DB_TAG_ID_COLUMN));
                tag.setTitle(jsonObject.getString(TagConstants.DB_TAG_TITLE_COLUMN));
                tag.setUserId(jsonObject.getLong(UserConstants.DB_USER_ID_COLUMN));
                tags.add(tag);
            }
        }
        return tags;
    }

}

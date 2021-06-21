package com.proximity.assignment.dao;

import com.proximity.assignment.model.Tag;

/**
 * @author sthammishetty on 18/06/21
 */
public interface TagDAO extends AbstractDAO<Tag> {

    void createTag(String title, Long userId);

    void deleteTag(Long tagId);

    void updateTag(Tag tag);

}

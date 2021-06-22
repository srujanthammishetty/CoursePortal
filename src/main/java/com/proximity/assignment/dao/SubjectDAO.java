package com.proximity.assignment.dao;

import com.proximity.assignment.model.Subject;

/**
 * @author sthammishetty on 18/06/21
 */
public interface SubjectDAO extends AbstractDAO<Subject> {


    Long createSubject(String name, Long userId);

    void deleteSubject(Long subjectId);

    void addSubjectToCourse(Long courseId, Long subjectId);

    void updateSubject(Subject subject);

}

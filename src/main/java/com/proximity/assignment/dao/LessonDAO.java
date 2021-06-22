package com.proximity.assignment.dao;

import com.proximity.assignment.model.Lesson;

import java.util.List;

/**
 * @author sthammishetty on 18/06/21
 */
public interface LessonDAO extends AbstractDAO<Lesson>{

    Long createLesson(String name, Long userId);

    void deleteLesson(Long lessonId);

    void updateLesson(Lesson lesson);

    void addLessonToCourse(Long lessonId, Long courseId);

    void updateLessonActiveStatus(Long lessonId, Long courseId, boolean isActive);

    List<Lesson> getActiveLessons(Long courseId);
}

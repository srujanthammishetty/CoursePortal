package com.proximity.assignment.dao;

import com.proximity.assignment.model.Course;

import java.util.List;

/**
 * @author sthammishetty on 18/06/21
 */
public interface CourseDAO extends AbstractDAO<Course> {

    void createCourse(String courseName, Long userId);

    void deleteCourse(Long courseId);

    void updateCourse(Course course);

    void subscribeToCourse(Long userId, Long courseId);

    void unsubscribeToCourse(Long userId, Long courseId);

    List<Course> getAllActiveCourses();

    List<Course> getCourses(Long subjectId);

}

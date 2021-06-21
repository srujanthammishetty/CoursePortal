package com.proximity.assignment.api;

import com.proximity.assignment.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

;

/**
 * @author sthammishetty on 20/06/21
 */

@Configuration
public class ApplicationConfiguration {

    @Bean
    public UserHandler getUserHandler() {
        return new UserHandler();
    }

    @Bean
    public CourseHandler getCourseHandler() {
        return new CourseHandler();
    }

    @Bean
    public SubjectHandler getSubjectHandler() {
        return new SubjectHandler();
    }

    @Bean
    public LessonHandler getLessonHandler() {
        return new LessonHandler();
    }

    @Bean
    public VideoHandler getVideoHandler() {
        return new VideoHandler();
    }

    @Bean
    public TagHandler getTagHandler() {
        return new TagHandler();
    }

    @Bean
    public ResponseHandler getResponseHandler() {
        return new ResponseHandler();
    }

    @Bean
    public ErrorHandler getErrorHandler() {
        return new ErrorHandler();
    }

    @Bean
    public MDCHandler getMDCHandler() {
        return new MDCHandler();
    }
}

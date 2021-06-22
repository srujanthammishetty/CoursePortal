CREATE TABLE users (
    user_id SERIAL UNIQUE,
    is_instructor boolean DEFAULT false,
    name text not null,
    email_id text PRIMARY KEY
);


CREATE TABLE courses (
    course_id SERIAL UNIQUE,
    name text PRIMARY KEY,
    is_active boolean DEFAULT false,
    user_id bigint, FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE subjects (
    subject_id SERIAL UNIQUE,
    name text PRIMARY KEY,
    user_id bigint, FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);



CREATE TABLE lessons (
    lesson_id SERIAL UNIQUE,
    name text PRIMARY KEY,
    user_id bigint, FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE videos (
    video_id SERIAL UNIQUE,
    link text NOT NULL,
    user_id bigint,
    title text PRIMARY KEY,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE tags (
    tag_id SERIAL UNIQUE,
    title text PRIMARY KEY,
    user_id bigint, FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE course_subjects (
    id SERIAL,
    course_id bigint,
    subject_id bigint,
    PRIMARY KEY (course_id, subject_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);


CREATE TABLE video_tags (
    id SERIAL,
    video_id bigint ,
    tag_id bigint REFERENCES tags(tag_id) ON DELETE CASCADE,
  	PRIMARY KEY (video_id, tag_id),
  	FOREIGN KEY(video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
  	FOREIGN KEY(tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);



CREATE TABLE video_lessons (
    id SERIAL,
    video_id bigint ,
    lesson_id bigint ,
    is_active boolean default true,
    PRIMARY KEY (video_id, lesson_id),
    FOREIGN KEY(video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    FOREIGN KEY(lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE
);


CREATE TABLE lesson_courses (
    id SERIAL,
    lesson_id bigint ,
    course_id bigint ,
    is_active boolean DEFAULT TRUE,
    PRIMARY KEY (lesson_id, course_id),
    FOREIGN KEY(lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    FOREIGN KEY(course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);


CREATE TABLE student_courses (
    id SERIAL,
    user_id bigint ,
    course_id bigint ,
    PRIMARY KEY (user_id, course_id),
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(course_id) REFERENCES courses(course_id) ON DELETE CASCADE

);

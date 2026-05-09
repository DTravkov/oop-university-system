package model.domain;

import model.enumeration.LessonType;
import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Lesson extends SerializableModel {

    private LessonType lessonType;
    private Course course;
    private Teacher teacher;
    private Date startTime;

    public Lesson(LessonType lessonType, Course course, Teacher teacher, Date startTime) {
        FieldValidator.requireNonNull(lessonType, "Lesson type");
        FieldValidator.requireNonNull(course, "Course");
        FieldValidator.requireNonNull(teacher, "Teacher");
        FieldValidator.requireNonNull(startTime, "Scheduled time");
        this.lessonType = lessonType;
        this.course = course;
        this.teacher = teacher;
        this.startTime = startTime;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        FieldValidator.requireNonNull(course, "Course");
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        FieldValidator.requireNonNull(teacher, "Teacher");
        this.teacher = teacher;
    }

    public int getCourseId() {
        return course.getId();
    }

    public int getTeacherId() {
        return teacher.getId();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;

        if (id != 0 && lesson.id != 0) {
            return id == lesson.id;
        }

        return lessonType == lesson.lessonType
                && Objects.equals(course, lesson.course)
                && Objects.equals(teacher, lesson.teacher)
                && Objects.equals(startTime, lesson.startTime);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(lessonType, course, teacher, startTime);
    }
}

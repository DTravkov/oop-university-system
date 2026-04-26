package model.domain;

import model.enumeration.LessonType;
import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Lesson extends SerializableModel{

    private LessonType lessonType;
    private int courseId;
    private int teacherId;
    private Date startTime;
    
    public Lesson(LessonType lessonType, int courseId, int teacherId, Date startTime) {
        FieldValidator.requireNonNull(lessonType, "Lesson type");
        FieldValidator.requirePositive(courseId, "Course ID");
        FieldValidator.requirePositive(teacherId, "Teacher ID");
        FieldValidator.requireNonNull(startTime, "Scheduled time");
        this.lessonType = lessonType;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.startTime = startTime;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

        return lessonType == lesson.lessonType &&
                courseId == lesson.courseId &&
                teacherId == lesson.teacherId &&
                Objects.equals(startTime, lesson.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lessonType, courseId, teacherId, startTime);
    }
}

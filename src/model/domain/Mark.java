package model.domain;

import utils.FieldValidator;

import java.util.Date;
import java.util.Objects;

public class Mark extends SerializableModel{


    private Student student;
    private Teacher teacher;
    private Course course;
    private Date time;

    public Mark(Student student, Teacher teacher, Course course) {
        FieldValidator validator = new FieldValidator();
        validator.requireNonNull(student, "Student")
                .requireNonNull(teacher, "Teacher")
                .requireNonNull(course, "Course")
                .validate();
        this.student = student;
        this.teacher = teacher;
        this.course = course;
        this.time = new Date();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        if(this.id != 0 && mark.getId() != 0) return this.id == mark.id;
        return Objects.equals(student, mark.student) &&
                Objects.equals(teacher, mark.teacher) &&
                Objects.equals(time, mark.getTime()) &&
                Objects.equals(course, mark.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, teacher, time, course);
    }

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + student +
                ", teacher=" + teacher +
                ", course=" + course +
                ", time= " + time +
                '}';
    }
}

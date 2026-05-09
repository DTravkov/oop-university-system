package model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.enumeration.CourseType;
import utils.FieldValidator;

public class Course extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private int credits;
    private CourseType type;
    private List<Teacher> lectureTeachers = new ArrayList<>();
    private List<Teacher> practiceTeachers = new ArrayList<>();

    public Course(String name, String description, int credits, CourseType type) {
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public List<Teacher> getLectureTeachers() {
        return List.copyOf(lectureTeachers);
    }

    public void addLectureTeacher(Teacher lectureTeacher) {
        FieldValidator.requireNonNull(lectureTeacher, "Lecture teacher");
        if (!this.lectureTeachers.contains(lectureTeacher)) {
            this.lectureTeachers.add(lectureTeacher);
        }
    }

    public List<Teacher> getPracticeTeachers() {
        return List.copyOf(practiceTeachers);
    }

    public void addPracticeTeacher(Teacher practiceTeacher) {
        FieldValidator.requireNonNull(practiceTeacher, "Practice teacher");
        if (!this.practiceTeachers.contains(practiceTeacher)) {
            this.practiceTeachers.add(practiceTeacher);
        }
    }

    public void removeLectureTeacher(int lectureTeacherId) {
        this.lectureTeachers.removeIf(t -> t.getId() == lectureTeacherId);
    }

    public void removePracticeTeacher(int practiceTeacherId) {
        this.practiceTeachers.removeIf(t -> t.getId() == practiceTeacherId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        if (this.id != 0 && course.getId() != 0) {
            return this.id == course.getId();
        }
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lectureTeachers=" + lectureTeachers +
                ", practiceTeachers=" + practiceTeachers +
                '}';
    }
}

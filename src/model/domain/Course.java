package model.domain;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
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

    private List<Enrollment> enrollments = new ArrayList<>();

    public Course(String name, String description, int credits, CourseType type) {
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.type = type;
    }


    

    public List<Enrollment> getEnrollments() {
        return List.copyOf(enrollments);
    }

    public void addEnrollment(Enrollment enrollment) {

        if(this.enrollments.contains(enrollment))
            throw new AlreadyExists("Enrollment with for " + enrollment.getStudent() + " on " + getName() + " course");

        if(this.enrollments.stream().anyMatch(e -> e.getStudent().getId() == enrollment.getStudent().getId()))
            throw new AlreadyExists("Enrollment with for " + enrollment.getStudent() + " on " + getName() + " course");

        if(!this.getLectureTeachers().contains(enrollment.getLectureTeacher()) || !this.getPracticeTeachers().contains(enrollment.getPracticeTeacher()))
            throw new DoesNotExist("Teacher does not lead the course");


        this.enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.removeIf(e -> e.getId() == enrollment.getId());
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
        if(!lectureTeacher.isLecturer())
            throw new OperationNotAllowed("Adding non-lecturer as a lecture teacher");
        if(lectureTeachers.contains(lectureTeacher)){
            throw new AlreadyExists("teacher with id=" + lectureTeacher.getId() + " as a lecture teacher");
        }
        if (!this.lectureTeachers.contains(lectureTeacher)) {
            this.lectureTeachers.add(lectureTeacher);
        }
    }

    public List<Teacher> getPracticeTeachers() {
        return List.copyOf(practiceTeachers);
    }

    public void addPracticeTeacher(Teacher practiceTeacher) {
        FieldValidator.requireNonNull(practiceTeacher, "Practice teacher");
        if(!practiceTeacher.isPractice())
            throw new OperationNotAllowed("Adding non-practice as a lecture teacher");
        if(lectureTeachers.contains(practiceTeacher)){
            throw new AlreadyExists("teacher with id=" + practiceTeacher.getId() + " as a practice teacher");
        }
        if (!this.practiceTeachers.contains(practiceTeacher)) {
            this.practiceTeachers.add(practiceTeacher);
        }
    }

    public void removeLectureTeacher(Teacher lectureTeacher) {
        this.lectureTeachers.removeIf(t -> t.getId() == lectureTeacher.getId());
    }

    public void removePracticeTeacher(Teacher practiceTeacher) {
        this.practiceTeachers.removeIf(t -> t.getId() == practiceTeacher.getId());
    }

    @Override
    public String asLine() {
        return String.format("ID: %d | Title: %s | Type: %s | Credits: %d",
                id, name, type, credits);
    }

    @Override
    public String asTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append('\n');
        sb.append("Title: ").append(name).append('\n');
        sb.append("Description: ").append(description).append('\n');
        sb.append("Type: ").append(type).append('\n');
        sb.append("Credits: ").append(credits).append('\n');
        sb.append("/Lecture teachers/\n");
        for (Teacher t : lectureTeachers) {
            sb.append(t.asLine()).append('\n');
        }
        sb.append("/Practice teachers/\n");
        for (Teacher t : practiceTeachers) {
            sb.append(t.asLine()).append('\n');
        }
        sb.append("/Enrollments count/\n").append(enrollments.size()).append('\n');
        return sb.toString();
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

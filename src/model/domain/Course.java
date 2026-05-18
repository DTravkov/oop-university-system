package model.domain;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;
import model.enumeration.CourseType;
import model.enumeration.TeacherType;
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


    public void addTeacher(Teacher teacher, TeacherType typeToAdd){
        if(typeToAdd == TeacherType.BOTH)
            throw new OperationNotAllowed("TeacherType BOTH cannot be used when adding a teacher.");

        if(typeToAdd == TeacherType.LECTURE){
            addLectureTeacher(teacher);
        }
        else if(typeToAdd == TeacherType.PRACTICE){
            addPracticeTeacher(teacher);
        }
    }


    public void removeTeacher(Teacher teacher, TeacherType typeToDelete){
        if(typeToDelete == TeacherType.BOTH)
            throw new OperationNotAllowed("TeacherType BOTH cannot be used when adding a teacher.");
        if(typeToDelete == TeacherType.LECTURE){
            removeLectureTeacher(teacher);
        }
        else if(typeToDelete == TeacherType.PRACTICE){
            removePracticeTeacher(teacher);
        }
        return;
    }

    private void addLectureTeacher(Teacher lectureTeacher) {
        FieldValidator.requireNonNull(lectureTeacher);
        if(!lectureTeacher.isLecturer())
            throw new OperationNotAllowed("Only lecturers can be assigned as lecture teachers.");
        if(lectureTeachers.contains(lectureTeacher)){
            throw new AlreadyExists("This teacher is already assigned as a lecture teacher on this course.");
        }
        this.lectureTeachers.add(lectureTeacher);
        
    }

    private void addPracticeTeacher(Teacher practiceTeacher) {
        FieldValidator.requireNonNull(practiceTeacher);

        if(!practiceTeacher.isPractice())
            throw new OperationNotAllowed("Only practice teachers can be assigned as practice teachers.");

        if(practiceTeachers.contains(practiceTeacher)){
            throw new AlreadyExists("This teacher is already assigned as a practice teacher on this course.");
        }
        
        this.practiceTeachers.add(practiceTeacher);
        
    }

    public List<Teacher> getPracticeTeachers() {
        return List.copyOf(practiceTeachers);
    }

    public List<Teacher> getLectureTeachers() {
        return List.copyOf(lectureTeachers);
    }

    private void removeLectureTeacher(Teacher lectureTeacher) {
        this.lectureTeachers.removeIf(t -> t.getId() == lectureTeacher.getId());
    }

    private void removePracticeTeacher(Teacher practiceTeacher) {
        this.practiceTeachers.removeIf(t -> t.getId() == practiceTeacher.getId());
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


    public void detachTeacher(Teacher teacher){
        removeLectureTeacher(teacher);
        removePracticeTeacher(teacher);
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

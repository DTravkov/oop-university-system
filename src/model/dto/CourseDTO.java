package model.dto;

import model.domain.Course;
import model.enumeration.CourseType;

import java.util.List;

public final class CourseDTO extends BaseViewDTO {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int credits;
    private final CourseType type;
    private final List<UserDTO> lectureTeachers;
    private final List<UserDTO> practiceTeachers;

    public CourseDTO(int id, String name, String description, int credits, CourseType type,
                     List<UserDTO> lectureTeachers, List<UserDTO> practiceTeachers) {
        super();
        if (id != 0) {
            setId(id);
        }
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.type = type;
        this.lectureTeachers = lectureTeachers == null ? List.of() : List.copyOf(lectureTeachers);
        this.practiceTeachers = practiceTeachers == null ? List.of() : List.copyOf(practiceTeachers);
    }

    public CourseDTO(Course course, List<UserDTO> lectureTeachers, List<UserDTO> practiceTeachers) {
        this(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCredits(),
                course.getType(),
                lectureTeachers,
                practiceTeachers);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCredits() {
        return credits;
    }

    public CourseType getType() {
        return type;
    }

    public List<UserDTO> getLectureTeachers() {
        return lectureTeachers;
    }

    public List<UserDTO> getPracticeTeachers() {
        return practiceTeachers;
    }

    @Override
    public String toShortString() {
        return "ID: " + getId() + " | Name: " + name;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append("\nID: ").append(getId());
        body.append("\nName: ").append(name);
        body.append("\nDescription: ").append(description);
        body.append("\nCredits: ").append(credits);
        body.append("\nType: ").append(type);
        body.append("\nLecture teachers: ").append(formatUserList(lectureTeachers));
        body.append("\nPractice teachers: ").append(formatUserList(practiceTeachers));
        return section("Course", body.toString());
    }
}

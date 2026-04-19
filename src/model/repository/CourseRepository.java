package model.repository;

import model.domain.Course;

public class CourseRepository extends Repository<Course> {

    public CourseRepository() {
        super("courses.ser");
    }

    public Course getByName(String name) {
        return this.data.values().stream()
                .filter(entity -> entity.getName()
                .equals(name))
                .findFirst()
                .orElse(null);
    }
}

package model.repository;

import model.domain.Course;

public class CourseRepository extends Repository<Course> {

    private static final CourseRepository INSTANCE = new CourseRepository();

    private CourseRepository() {
        super();
    }

    public static CourseRepository getInstance() {
        return INSTANCE;
    }

    public Course findByName(String name) {
        return this.data.values().stream()
                .filter(entity -> entity.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
}

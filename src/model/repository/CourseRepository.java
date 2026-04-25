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
        return this.findFirst(entity -> entity.getName().equals(name))
                .orElse(null);
    }

    public boolean existsByName(String name){
        return exists(course -> course.getName().equals(name));
    }
    
}

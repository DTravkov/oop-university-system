package services;

import model.domain.Course;
import model.repository.CourseRepository;

public class CourseService extends BaseService<Course, CourseRepository> {

    public CourseService() {
        super(new CourseRepository());
    }

    public void createCourse(Course course) {
        throwIfExist(course.getId());
        repository.save(course);
    }

    public void updateCourse(Course oldCourse, Course updatedCourse) {
        get(oldCourse.getId());
        if(updatedCourse.getId() != 0){
            throwIfExist(updatedCourse.getId());
        }
        repository.save(updatedCourse);
    }


}

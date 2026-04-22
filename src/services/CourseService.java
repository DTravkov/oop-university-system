package services;

import model.domain.Course;
import model.repository.CourseRepository;

public class CourseService extends BaseService<Course, CourseRepository> {

    public CourseService() {
        super(CourseRepository.getInstance());
    }

}

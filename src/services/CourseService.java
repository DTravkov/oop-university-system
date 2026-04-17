package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.Course;
import model.CourseRepository;

public class CourseService extends BaseService<Course, CourseRepository> {

    public CourseService() {
        super(new CourseRepository());
    }

    public CourseService(CourseRepository repository) {
        super(repository);
    }

    public void createCourse(Course course) {
        ensureNotExists(course.getName());
        repository.add(course);
    }

    public void updateCourse(Course oldCourse, Course updatedCourse) {

        if (!oldCourse.getName().equalsIgnoreCase(updatedCourse.getName())) {
            ensureNotExists(updatedCourse.getName());
        }

        repository.update(oldCourse, updatedCourse);
    }

    public Course findOrThrow(String name) {
        Course course = repository.getByName(name);

        if (course == null) {
            throw new DoesNotExist("Course '" + name + "'");
        }

        return course;
    }

    public void deleteCourse(String name) {
        Course course = findOrThrow(name);
        repository.delete(course);
    }

    private void ensureNotExists(String name) {
        if (repository.getByName(name) != null) {
            throw new AlreadyExists("Course '" + name + "'");
        }
    }
}

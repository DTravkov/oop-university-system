package services;

import java.util.Collection;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.Course;
import model.CourseRepository;

public class CourseService {
    private static final CourseRepository repo = new CourseRepository();

    public void createCourse(Course course) {
    	this.ensureNotExists(course.getName());
        this.repo.add(course);
    }

    public void updateCourse(Course old, Course updated) {
    	this.ensureNotExists(updated.getName());
        this.repo.update(old, updated);
    }

    public void deleteCourse(String name) {
        Course course = this.getByName(name);
        this.repo.delete(course);
    }

    public boolean ensureNotExists(String name) {
        if (this.repo.getByName(name) != null) throw new AlreadyExists("Course with this title already exists.");
        return true;
    }
    
    public Course getByName(String name) {
        Course course = this.repo.getByName(name);

        if (course == null) {
            throw new DoesNotExist("Course with name " + name + " not found.");
        }
        
        return course;
    }
    
    
    public Collection<Course> getAll() {
    	return this.repo.getAll();
    }

    public CourseRepository getRepository() {
        return repo;
    }
}
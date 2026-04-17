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
    	if(!old.getName().equalsIgnoreCase(updated.getName())) {
    		this.ensureNotExists(updated.getName());
    	}
        this.repo.update(old, updated);
    }

    public void deleteCourse(String name) {
        Course course = this.getCourse(name);
        this.repo.delete(course);
    }

    private boolean ensureNotExists(String name) {
        if (this.repo.getByName(name) != null) 
        	throw new AlreadyExists("Course with this title already exists.");
        return true;
    }
    
    public Collection<Course> getAll() {
    	return this.repo.getAll();
    }
    
    public Course getCourse(String name) {
        Course course = this.repo.getByName(name);

        if (course == null) {
            throw new DoesNotExist("Course with name " + name + " not found.");
        }
        
        return course;
    }
    
    public Course getCourse(int id) {
        Course course = this.repo.getById(id);

        if (course == null) {
            throw new DoesNotExist("Course with id " + id + " not found.");
        }
        
        return course;
    }
}
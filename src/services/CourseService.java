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
    	this.throwIfExists(course.getName());
        this.repository.add(course);
    }

    public void updateCourse(Course old, Course updated) {
    	if(!old.getName().equalsIgnoreCase(updated.getName())) {
    		this.throwIfExists(updated.getName());
    	}
        this.repository.update(old, updated);
    }
    
    
    public Course findOrThrow(String name) {
        Course course = this.repository.getByName(name);
        if (course == null) {
            throw new DoesNotExist();
        }
        return course;
    }

    public void deleteCourse(String name) {
        Course course = this.findOrThrow(name);
        this.repository.delete(course);
    }
    
    private boolean throwIfExists(String name) {
        Course course = this.repository.getByName(name);
        if(course != null) throw new AlreadyExists();
        return true;
    }
   
}
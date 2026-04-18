package model.repository;

import model.domain.Course;

public class CourseRepository extends BaseRepository<Course> {
    @Override
    protected String getFilePath() {
        return "courses.ser";
    }
    

    public Course getByName(String name) {
        for(Course c : this.getAll()) {
        	if(c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }
    
}
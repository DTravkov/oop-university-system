package model;

public class CourseRepository extends BaseRepository<Course> {
    @Override
    protected String getFilePath() {
        return "coruses.ser";
    }
    

    public Course getByName(String name) {
        for(Course c : this.getAll()) {
        	if(c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }
    
}
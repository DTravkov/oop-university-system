package services.events.concrete;

import model.domain.Course;
import services.events.interfaces.CourseEvent;

public record CourseDeleteEvent(Course course) implements CourseEvent{

    public Course getCourse(){
        return course;
    }

}

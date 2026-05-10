package services.events;

import model.domain.Course;

public record CourseDeleteEvent(Course course) implements Event{

    public Course getCourse(){
        return course;
    }

}

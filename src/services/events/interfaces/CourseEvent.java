package services.events.interfaces;

import model.domain.Course;

public interface CourseEvent extends Event {
    public Course getCourse();
}

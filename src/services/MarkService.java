package services;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Mark;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.repository.MarkRepository;

public class MarkService extends BaseService<Mark, MarkRepository> {

    private final UserService userService;
    private final CourseService courseService;

    public MarkService() {
        super(new MarkRepository());
        this.userService = new UserService();
        this.courseService = new CourseService();
    }

    public void createMark(Mark mark) {
        User student = userService.get(mark.getStudent().getId());
        if (!(student instanceof Student validStudent)) {
            throw new OperationNotAllowed("Cannot assign a mark to a non-student user");
        }

        User teacher = userService.get(mark.getTeacher().getId());
        if (!(teacher instanceof Teacher validTeacher)) {
            throw new OperationNotAllowed("Cannot assign a mark by a non-teacher user");
        }

        Course course = courseService.get(mark.getCourse().getId());
        mark.setStudent(validStudent);
        mark.setTeacher(validTeacher);
        mark.setCourse(course);

        repository.save(mark);
    }
}

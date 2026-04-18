package services;

import java.util.Collection;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Mark;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.repository.MarkRepository;

public class MarkService {

    protected final MarkRepository repository;
    private final UserService userService;
    private final CourseService courseService;

    public MarkService() {
        this(new MarkRepository(), new UserService(), new CourseService());
    }

    public MarkService(MarkRepository repository, UserService userService, CourseService courseService) {
        this.repository = repository;
        this.userService = userService;
        this.courseService = courseService;
    }

    public void createMark(Mark mark) {

        User student = userService.findOrThrow(mark.getStudent().getId());
        User teacher = userService.findOrThrow(mark.getTeacher().getId());
        Course course = courseService.findOrThrow(mark.getCourse().getId());

        if (!(student instanceof Student validStudent)) {
            throw new OperationNotAllowed(" create mark for a User who is not a student");
        }
        if (!(teacher instanceof Teacher validTeacher)) {
            throw new OperationNotAllowed(" create mark by a User who is not a teacher");
        }

        mark.setStudent(validStudent);
        mark.setTeacher(validTeacher);
        mark.setCourse(course);

        ensureNotExists(validStudent.getId(), mark);
        repository.add(validStudent.getId(), mark);
    }

    public void deleteMark(Mark mark) {
        findOrThrow(mark.getStudent().getId(), mark.getId());
        repository.delete(mark.getStudent().getId(), mark);
    }

    public Mark findOrThrow(int studentId, int markId) {
        for (Mark mark : repository.getAllByOwnerId(studentId)) {
            if (mark.getId() == markId) {
                return mark;
            }
        }
        throw new DoesNotExist("Mark with id " + markId);
    }

    public Collection<Mark> getAll() {
        return repository.getAll();
    }

    public Collection<Mark> getAllByStudentId(int studentId) {
        userService.findOrThrow(studentId);
        return repository.getAllByOwnerId(studentId);
    }

    private void ensureNotExists(int studentId, Mark target) {
        for (Mark mark : repository.getAllByOwnerId(studentId)) {
            if (mark.equals(target)) {
                throw new AlreadyExists("Mark");
            }
        }
    }
}

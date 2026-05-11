package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.AlreadyExists;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.AttestationType;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class EnrollmentService extends BaseService<Enrollment>  {

    public EnrollmentService() {
        super(Enrollment.class);
        subscribeToEvents();
    }


    @Override
    public Enrollment create(Enrollment enrollment) {
        if(this.find(e -> e.getStudent().getId() == enrollment.getStudent().getId() 
            && e.getCourse().getId() == enrollment.getCourse().getId()) != null){
            throw new AlreadyExists("Enrollment to this course for this student");
        }
        return super.create(enrollment);
    }

    public void addPoints(Enrollment enrollment, Teacher teacher, AttestationType att, double points){
        enrollment.incrementMark(points, teacher, att);
        this.update(enrollment);
    }


    public double getStudentGpa(Student student) {
        double totalGpa = 0.0;
        List<Enrollment> enrollments = getStudentEnrollments(student);
        if(enrollments.isEmpty()) return 0.0;
        for(var enr : enrollments){
            totalGpa += enr.getGpa();
        }
        totalGpa /= enrollments.size();
        return Math.round(totalGpa * 100.0 ) / 100.0;
    }

    public List<Enrollment> getStudentEnrollments(Student student) {
        return getAll().stream()
                .filter(e -> e.getStudent().getId() == student.getId())
                .toList();
    }

    public List<Enrollment> getCourseEnrollments(Course course){
        return getAll().stream()
                       .filter(e -> e.getCourse().getId() == course.getId())
                       .toList();
    }

    public Map<Course, List<Enrollment>> getTeacherEnrollments(Teacher teacher) {
        Map<Course, List<Enrollment>> map = new HashMap<>();
        List<Enrollment> allEnrollments = getAll().stream()
                                        .filter(e -> e.isTeaching(teacher))
                                        .toList();
        for(var enr : allEnrollments){
            map.computeIfAbsent(enr.getCourse(), (k) -> new ArrayList<>()).add(enr);
        }
        return map;
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));
    }

    public void onUserDelete(User user) {
        if(user instanceof Student){
            for (Enrollment enr : getAll()) {
                if(enr.getStudent().getId() == user.getId()) this.delete(enr);
            }
            repository.saveAll();
        }
        if (user instanceof Teacher) {
            for (Enrollment enr : getAll()) {
                if (enr.getLectureTeacher().getId() == user.getId()) enr.setLectureTeacher(AppSettings.DELETED_TEACHER);
                if (enr.getPracticeTeacher().getId() == user.getId()) enr.setPracticeTeacher(AppSettings.DELETED_TEACHER);
            }
            repository.saveAll();
        }

    }




}

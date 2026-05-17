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
import services.events.concrete.CourseDeleteEvent;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;

/**
 * EnrollmentService is a concrete service. It implements logic for enrollments. 
 * Enrollments are a big part of our system, they connect all the course, teachers, students, and their scores.
 */
public class EnrollmentService extends GenericService<Enrollment>  {

    public EnrollmentService() {
        super(Enrollment.class);
    }

    // CREATE / UPDATE / DELETE

    @Override
    public Enrollment create(Enrollment enrollment) {
        if(this.find(e -> e.getStudent().equals(enrollment.getStudent()) 
            && e.getCourse().equals(enrollment.getCourse())) != null){
            throw new AlreadyExists("Enrollment to this course for this student");
        }
        return super.create(enrollment);
    }

    public void addPoints(Enrollment enrollment, Teacher teacher, AttestationType att, double points){
        enrollment.incrementMark(points, teacher, att);
        Logger.log("Add points (" + points + ") by teacher (" + teacher.asLine() + ") to enrollment (" + enrollment.getId() + ") for attestation (" + att + ")");
        repository.save(enrollment);
    }


    // QUERIES

    public double getGpaByStudent(Student student) {
        double totalGpa = 0.0;
        List<Enrollment> enrollments = getEnrollmentsByStudent(student);
        if(enrollments.isEmpty()) return 0.0;
        for(var enr : enrollments){
            totalGpa += enr.getGpa();
        }
        totalGpa /= enrollments.size();
        return Math.round(totalGpa * 100.0 ) / 100.0;
    }

    public List<Enrollment> getEnrollmentsByStudent(Student student) {
        return getAll(e -> e.getStudent().getId() == student.getId());
    }

    public List<Enrollment> getEnrollmentsByCourse(Course course){
        return getAll(e -> e.getCourse().getId() == course.getId());
    }

    public Map<Course, List<Enrollment>> getEnrollmentsByTeacher(Teacher teacher) {
        Map<Course, List<Enrollment>> map = new HashMap<>();
        for(Enrollment enr : getAll(e -> e.isTeaching(teacher))){
            map.computeIfAbsent(enr.getCourse(), (k) -> new ArrayList<>()).add(enr);
        }
        return map;
    }


    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));
        eventSystem.subscribe(CourseDeleteEvent.class, event -> onCourseDelete(event.getCourse()));
    }

    public void onUserDelete(User user) {
        if(user instanceof Student student){
            getEnrollmentsByStudent(student).forEach(e -> delete(e));
            repository.saveAll();
        }
    }

    public void onCourseDelete(Course course){
        getEnrollmentsByCourse(course).forEach(enr -> delete(enr));
    }




}

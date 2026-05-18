package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.AlreadyExists;
import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.AttestationType;
import services.events.concrete.CourseDeleteEvent;
import services.events.concrete.UserDeleteEvent;
import utils.Logger;
import utils.UIText;

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
        if(existsByStudentAndCourse(enrollment)){
            throw new AlreadyExists(UIText.ERR_ALREADY_ENROLLED);
        }
        List<Enrollment> studentEnrollments = getEnrollments(enrollment.getStudent());
        int creditCount = 0;
        for(var enr : studentEnrollments){
            creditCount += enr.getCourse().getCredits();
        }
        if(creditCount + enrollment.getCourse().getCredits() > 21){
            throw new OperationNotAllowed(UIText.ERR_CREDITS_EXCEED_21);
        }
        
        return super.create(enrollment);
    }

    public void addPoints(Enrollment enrollment, Teacher teacher, AttestationType att, double points){
        enrollment.incrementMark(points, teacher, att);
        Logger.log("Add points (" + points + ") by teacher (" + teacher.asLine() + ") to enrollment (" + enrollment.getId() + ") for attestation (" + att + ")");
        repository.save(enrollment);
    }


    // QUERIES

    /**
     * checks whether enrollment with the same student+course exist.
     * @param enrollment
     * @return
     */
    public boolean existsByStudentAndCourse(Enrollment enrollment){
        return this.find(e -> e.getStudent().equals(enrollment.getStudent()) 
            && e.getCourse().equals(enrollment.getCourse())) != null;
    }

    public double getGpaByStudent(Student student) {
        double totalGpa = 0.0;
        List<Enrollment> enrollments = getEnrollments(student);
        if(enrollments.isEmpty()) return 0.0;
        for(var enr : enrollments){
            totalGpa += enr.getGpa();
        }
        totalGpa /= enrollments.size();
        return Math.round(totalGpa * 100.0 ) / 100.0;
    }

    public List<Enrollment> getEnrollments(Student student) {
        return getAll(e -> e.getStudent().getId() == student.getId());
    }

    public List<Enrollment> getEnrollments(Course course){
        return getAll(e -> e.getCourse().getId() == course.getId());
    }

    public Map<Course, List<Enrollment>> getEnrollments(Teacher teacher) {
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
            getEnrollments(student).forEach(e -> delete(e));
            repository.saveAll();
        }
    }

    public void onCourseDelete(Course course){
        getEnrollments(course).forEach(enr -> delete(enr));
    }




}

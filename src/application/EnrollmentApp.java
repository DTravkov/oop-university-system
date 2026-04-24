package application;

import java.util.Scanner;

import exceptions.ApplicationException;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Student;
import model.domain.User;
import model.enumeration.UIMessages;
import model.enumeration.UserRole;
import services.CourseService;
import services.EnrollmentService;
import services.LanguageService;
import services.UserService;
import utils.UIFields;

public class EnrollmentApp {

    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final UserService userService = new UserService();
    private static final CourseService courseService = new CourseService();

    public static void startApp(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = UIFields.readChoice(scanner, UIMessages.CHOOSE, 1, 6);

            try {
                switch (choice) {
                    case "1":
                        createEnrollment(scanner);
                        break;
                    case "2":
                        deleteEnrollment(scanner);
                        break;
                    case "3":
                        getEnrollmentsOfStudent(scanner);
                        break;
                    case "4":
                        getEnrollmentsOfCourse(scanner);
                        break;
                    case "5":
                        getAllEnrollments();
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println(LanguageService.translate(UIMessages.INVALID_CHOICE));
                }
            } catch (ApplicationException e) {
                System.out.println(LanguageService.translate(e.getMessageKey(), e.getArgs()));
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- " + LanguageService.translate(UIMessages.TITLE_ENROLL) + " ---");
        System.out.println("1. " + LanguageService.translate(UIMessages.ENROLL));
        System.out.println("2. " + LanguageService.translate(UIMessages.DROP));
        System.out.println("3. " + LanguageService.translate(UIMessages.VIEW_STUDENT));
        System.out.println("4. Get enrollments by course");
        System.out.println("5. " + LanguageService.translate(UIMessages.VIEW_ALL));
        System.out.println("6. " + LanguageService.translate(UIMessages.EXIT));
    }

    private static void createEnrollment(Scanner scanner) {
        printStudents();
        printCourses();
        int studentId = UIFields.readInt(scanner, UIMessages.STUDENT_ID);
        int courseId = UIFields.readInt(scanner, UIMessages.COURSE_ID);

        Enrollment enrollment = new Enrollment(courseId, studentId);
        enrollmentService.create(enrollment);

        System.out.println(LanguageService.translate(UIMessages.CREATED));
        System.out.println(enrollment);
        System.out.println(enrollmentService.getAll());
    }

    private static void getEnrollmentsOfStudent(Scanner scanner) {
        printStudents();
        int studentId = UIFields.readInt(scanner, UIMessages.STUDENT_ID);
        System.out.println(enrollmentService.getAllByStudentId(studentId));
    }

    private static void deleteEnrollment(Scanner scanner) {
        int enrollmentId = UIFields.readInt(scanner, UIMessages.MESSAGE_ID);
        enrollmentService.delete(enrollmentId);

        System.out.println(LanguageService.translate(UIMessages.DELETED));
    }

    private static void getEnrollmentsOfCourse(Scanner scanner) {
        int courseId = UIFields.readInt(scanner, UIMessages.COURSE_ID);
        System.out.println(enrollmentService.getAllByCourseId(courseId));
    }

    private static void getAllEnrollments() {
        System.out.println(enrollmentService.getAll());
    }

    private static void printStudents() {
        System.out.println("--- Students ---");
        for (User user : userService.getAllByRole(UserRole.STUDENT)) {
            Student student = (Student) user;
            System.out.println("ID: " + student.getId() + ", Name: " + student.getName() + ", Surname: " + student.getSurname());
        }
    }

    private static void printCourses() {
        System.out.println("--- Courses ---");
        for (Course course : courseService.getAll()) {
            System.out.println("ID: " + course.getId() + ", Name: " + course.getName());
        }
    }
}

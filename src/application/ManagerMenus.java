package application;

import java.util.List;
import java.util.Map;

import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Manager;
import model.domain.News;
import model.domain.Student;
import model.domain.Teacher;
import model.enumeration.CourseType;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.CourseService;
import services.EnrollmentService;
import services.NewsService;
import services.TeacherService;
import services.UserService;
import utils.UIForms;

public class ManagerMenus extends BaseApp {

    static final UserService userService = services.userService;
    static final CourseService courseService = services.courseService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final NewsService newsService = services.newsService;
    static final TeacherService teacherService = services.teacherService;


    static MenuBuilder getManagerMenu() {
        MenuBuilder menu = new MenuBuilder("Manager Menu");
        menu.addAction("View all courses", () -> CommonMenus.printAllCourses());
        menu.addAction("Create course", () -> createCourse());
        menu.addAction("Delete course", () -> deleteCourse());
        menu.addAction("Assign teacher to a course", () -> assignTeacherToCourse());

        menu.addAction("View all news", () -> printAllNews());
        menu.addAction("Create news", () -> createNews());
        menu.addAction("Delete news", () -> deleteNews());

        menu.addAction("Get Student GPA statistics", () -> printStudentGPAStats());
        menu.addAction("Get Course GPA statistics", () -> printCourseGPAStats());
        menu.addAction("Get Teacher GPA statistics", () -> printTeacherGPAStats());

        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void createCourse() {
            String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_NAME);
            String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
            int credits = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_CREDITS);
            CourseType type = UIForms.readCourseType(scanner);
            Course course = new Course(name, description, credits, type);
            courseService.create(course);
            printSuccess("Course created.");
    }

    private static void deleteCourse() {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                println("No courses.");
                return;
            }
            printHeader("Courses");
            courses.forEach(c -> println(c.asLine()));
            Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);
            courseService.delete(course);
            printSuccess("Course deleted.");
    }

    private static void assignTeacherToCourse() {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                println("No courses.");
                return;
            }
            courses.forEach(c -> {
                println("\n" + c.asTable());
            });
            Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);
            List<Teacher> teachers = userService.getUsersByClass(Teacher.class);
            if (teachers.isEmpty()) {
                println("No teachers.");
                return;
            }
            printHeader("Teachers");
            teachers.forEach(t -> println(t.asLine()));
            Teacher teacher = UIForms.readIdFromList(scanner, UIMessage.INPUT_TEACHER_ID, teachers);
            TeacherType role = UIForms.readLectureOrPractice(scanner);
            courseService.addTeacher(course, teacher, role);
            printSuccess("Teacher assigned to course.");
    }

    private static void printAllNews() {
        List<News> all = newsService.getAll();
        if (all.isEmpty()) {
            println("No news.");
            return;
        }
        printHeader("News");
        all.forEach(n -> println(n.asLine()));
    }

    private static void createNews() {
            Manager manager = (Manager) getActiveUser();
            String title = UIForms.readNonEmpty(scanner, "News title: ");
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
            NewsUrgencyLevel urgency = UIForms.readNewsUrgencyLevel(scanner);
            News news = new News(manager, title, content, urgency);
            newsService.create(news);
            printSuccess("News posted.");
    }

    private static void deleteNews() {
            Manager manager = (Manager) getActiveUser();
            List<News> all = newsService.getAll();
            if (all.isEmpty()) {
                println("No news.");
                return;
            }
            printHeader("News");
            all.forEach(n -> println(n.asLine()));
            News news = UIForms.readIdFromList(scanner, UIMessage.INPUT_NEWS_ID, all);
            newsService.delete(news, manager);
            printSuccess("News deleted.");
    }

    private static void printStudentGPAStats() {
        List<Student> students = userService.getUsersByClass(Student.class);
        if (students.isEmpty()) {
            println("No students.");
            return;
        }
        printHeader("Average GPA by student");
        int totalGPA = 0;
        int totalStudents = 0;
        for (Student student : students) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
            if (enrollments.isEmpty()) {
                println(student.asLine() + " | no enrollments");
                continue;
            }
            double gpa = 0;
            for(var enr : enrollments){
                gpa += enr.getGpa();
            }
            gpa /= enrollments.size();
            totalGPA += gpa;
            totalStudents += 1;
            println(student.asLine() + " | Avg GPA: " + String.format("%.2f", gpa));
        }
        if(totalStudents != 0)
            println("Overall average GPA: " + totalGPA / totalStudents);
    }

    private static void printCourseGPAStats() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            println("No courses.");
            return;
        }
        printHeader("Average enrollment GPA by course");
        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course);
            if (enrollments.isEmpty()) {
                println(course.asLine() + " | no enrollments");
                continue;
            }
            double avg = enrollments.stream().mapToDouble(Enrollment::getGpa).average().orElse(0.0);
            println(course.asLine() + " | Avg GPA: " + String.format("%.2f", avg));
        }
    }

    private static void printTeacherGPAStats() {
        List<Teacher> teachers = userService.getUsersByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println("No teachers.");
            return;
        }
        printHeader("Average student GPA by teacher");
        for (Teacher teacher : teachers) {
            Map<Course, List<Enrollment>> byCourse = teacherService.getEnrollmentsByTeacher(teacher);
            double sum = 0.0;
            int count = 0;
            for (List<Enrollment> enrollments : byCourse.values()) {
                for (Enrollment enr : enrollments) {
                    sum += enr.getGpa();
                    count++;
                }
            }
            if (count == 0) {
                println(teacher.asLine() + " | no enrollments");
            } else {
                println(teacher.asLine() + " | Avg GPA: " + String.format("%.2f", sum / count));
            }
        }
    }
}

package application.apps;

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
import services.CourseService;
import services.EnrollmentService;
import services.NewsService;
import services.TeacherService;
import services.UserService;
import utils.UIForms;
import utils.UIText;

public final class ManagerApp extends BaseApp {

    static final UserService userService = services.userService;
    static final CourseService courseService = services.courseService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final NewsService newsService = services.newsService;
    static final TeacherService teacherService = services.teacherService;

    public ManagerApp() {
        super();
    }

    public static MenuBuilder getMenu() {
        return new MenuBuilder(UIText.MANAGER_MENU_TITLE)
                .addAction(UIText.MANAGER_VIEW_COURSES, () -> CommonMenus.printAllCourses())
                .addAction(UIText.MANAGER_CREATE_COURSE, () -> createCourse())
                .addAction(UIText.MANAGER_DELETE_COURSE, () -> deleteCourse())
                .addAction(UIText.MANAGER_ASSIGN_TEACHER, () -> assignTeacherToCourse())
                .addAction(UIText.MANAGER_VIEW_NEWS, () -> printAllNews())
                .addAction(UIText.MANAGER_CREATE_NEWS, () -> createNews())
                .addAction(UIText.MANAGER_DELETE_NEWS, () -> deleteNews())
                .addAction(UIText.MANAGER_GPA_BY_STUDENT, () -> printStudentGPAStats())
                .addAction(UIText.MANAGER_GPA_BY_COURSE, () -> printCourseGPAStats())
                .addAction(UIText.MANAGER_GPA_BY_TEACHER, () -> printTeacherGPAStats())
                .addExit();
    }

    private static void createCourse() {
        String name = UIForms.readNonEmpty(scanner, UIText.INPUT_COURSE_NAME);
        String description = UIForms.readNonEmpty(scanner, UIText.INPUT_COURSE_DESC);
        int credits = UIForms.readInt(scanner, UIText.INPUT_COURSE_CREDITS);
        CourseType type = UIForms.readCourseType(scanner);
        Course course = new Course(name, description, credits, type);
        courseService.create(course);
        printSuccess(UIText.MSG_COURSE_CREATED);
    }

    private static void deleteCourse() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            println(UIText.MSG_NO_COURSES);
            return;
        }
        printHeader(UIText.COURSE_HEADER_COURSES);
        courses.forEach(c -> println(c.asLine()));
        Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);
        courseService.delete(course);
        printSuccess(UIText.MSG_COURSE_DELETED);
    }

    private static void assignTeacherToCourse() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            println(UIText.MSG_NO_COURSES);
            return;
        }
        courses.forEach(c -> {
            println("\n" + c.asTable());
        });
        Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);
        List<Teacher> teachers = userService.getUsersByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println(UIText.MSG_NO_TEACHERS);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_TEACHERS);
        teachers.forEach(t -> println(t.asLine()));
        Teacher teacher = UIForms.readIdFromList(scanner, UIText.INPUT_TEACHER_ID, teachers);
        TeacherType role = UIForms.readLectureOrPractice(scanner);
        courseService.addTeacher(course, teacher, role);
        printSuccess(UIText.MSG_TEACHER_ASSIGNED);
    }

    private static void printAllNews() {
        List<News> all = newsService.getAll();
        if (all.isEmpty()) {
            println(UIText.MSG_NO_NEWS);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_NEWS);
        all.forEach(n -> println(n.asLine()));
    }

    private static void createNews() {
        Manager manager = (Manager) getActiveUser();
        String title = UIForms.readNonEmpty(scanner, UIText.INPUT_NEWS_TITLE);
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_MESSAGE_CONTENT);
        NewsUrgencyLevel urgency = UIForms.readNewsUrgencyLevel(scanner);
        News news = new News(manager, title, content, urgency);
        newsService.create(news);
        printSuccess(UIText.MSG_NEWS_POSTED);
    }

    private static void deleteNews() {
        Manager manager = (Manager) getActiveUser();
        List<News> all = newsService.getAll();
        if (all.isEmpty()) {
            println(UIText.MSG_NO_NEWS);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_NEWS);
        all.forEach(n -> println(n.asLine()));
        News news = UIForms.readIdFromList(scanner, UIText.INPUT_NEWS_ID, all);
        newsService.delete(news, manager);
        printSuccess(UIText.MSG_NEWS_DELETED);
    }

    private static void printStudentGPAStats() {
        List<Student> students = userService.getUsersByClass(Student.class);
        if (students.isEmpty()) {
            println(UIText.MSG_NO_STUDENTS);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_GPA_STUDENT);
        double totalGPA = 0.0;
        int totalStudents = 0;
        for (Student student : students) {
            List<Enrollment> enrollments = enrollmentService.getEnrollments(student);
            if (enrollments.isEmpty()) {
                println(UIText.MSG_NO_ENROLLMENTS_LINE, student.asLine());
                continue;
            }
            double gpa = 0;
            for (var enr : enrollments) {
                gpa += enr.getGpa();
            }
            gpa /= enrollments.size();
            totalGPA += gpa;
            totalStudents += 1;
            println(UIText.MSG_GPA_LINE, student.asLine(), String.format("%.2f", gpa));
        }
        if (totalStudents != 0) {
            println(UIText.MSG_OVERALL_AVG_GPA, String.format("%.2f", totalGPA / totalStudents));
        }
    }

    private static void printCourseGPAStats() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            println(UIText.MSG_NO_COURSES);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_GPA_COURSE);
        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentService.getEnrollments(course);
            if (enrollments.isEmpty()) {
                println(UIText.MSG_NO_ENROLLMENTS_LINE, course.asLine());
                continue;
            }
            double avg = enrollments.stream().mapToDouble(Enrollment::getGpa).average().orElse(0.0);
            println(UIText.MSG_GPA_LINE, course.asLine(), String.format("%.2f", avg));
        }
    }

    private static void printTeacherGPAStats() {
        List<Teacher> teachers = userService.getUsersByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println(UIText.MSG_NO_TEACHERS);
            return;
        }
        printHeader(UIText.MANAGER_HEADER_GPA_TEACHER);
        for (Teacher teacher : teachers) {
            Map<Course, List<Enrollment>> byCourse = teacherService.getEnrollments(teacher);
            double sum = 0.0;
            int count = 0;
            for (List<Enrollment> enrollments : byCourse.values()) {
                for (Enrollment enr : enrollments) {
                    sum += enr.getGpa();
                    count++;
                }
            }
            if (count == 0) {
                println(UIText.MSG_NO_ENROLLMENTS_LINE, teacher.asLine());
            } else {
                println(UIText.MSG_GPA_LINE, teacher.asLine(), String.format("%.2f", sum / count));
            }
        }
    }
}

package application;

import java.util.Comparator;
import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.Manager;
import model.domain.News;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import model.enumeration.CourseType;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import services.CourseService;
import services.EnrollmentService;
import services.NewsService;
import services.UserService;
import utils.Comparators;
import utils.StringUtils;
import utils.UIForms;

public final class ManagerApp extends BaseApp {

    private static final CourseService courseService = services.courseService;
    private static final NewsService newsService = services.newsService;
    private static final UserService userService = services.userService;
    private static final EnrollmentService enrollmentService = services.enrollmentService;

    private ManagerApp() {
    }

    public static void startApp() {
        if (!(getActiveUser() instanceof Manager)) {
            throw new OperationNotAllowed("accessing Manager Menu as a non-manager user");
        }

        ActionMenu menu = new ActionMenu("Manager Menu");
        menu.addAction("Manage courses", ManagerApp::startCoursesMenu);
        menu.addAction("Manage news", ManagerApp::startNewsMenu);
        menu.addAction("Statistics", ManagerApp::startStatisticsMenu);
        menu.addAction("Exit", menu::stop);
        menu.start();
    }


    private static void startCoursesMenu() {
        ActionMenu menu = new ActionMenu("Manage Courses");
        menu.addAction("View all courses", () -> handleExceptions(ManagerApp::printAllCourses));
        menu.addAction("View course details", () -> handleExceptions(ManagerApp::printCourseDetails));
        menu.addAction("Create course", () -> handleExceptions(ManagerApp::createCourse));
        menu.addAction("Delete course", () -> handleExceptions(ManagerApp::deleteCourse));
        menu.addAction("Add teacher to course", () -> handleExceptions(ManagerApp::addTeacherToCourse));
        menu.addAction("Remove teacher from course", () -> handleExceptions(ManagerApp::removeTeacherFromCourse));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void printCourseDetails() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        println(courseService.getDTO(courseId));
    }

    private static void printAllCourses() {
        List<Course> courses = courseService.getAll();
        if (courses.isEmpty()) {
            printFail("No courses found.");
            return;
        }
        println("\n||| Courses |||");
        courses.forEach(course -> println(courseService.getDTO(course).toShortString()));
    }

    private static void createCourse() {
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COURSE_DESC);
        int credits = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_CREDITS);
        CourseType type = UIForms.readCourseType(scanner);

        Course created = courseService.create(new Course(name, description, credits, type));
        printSuccess("Course created.");
        println(courseService.getDTO(created));
    }

    private static void deleteCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        courseService.delete(courseId);
        printSuccess("Course deleted.");
    }

    private static void addTeacherToCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);

        TeacherType type = UIForms.readLectureOrPractice(scanner);
        printTeachersByType(type);

        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);
        courseService.addTeacher(courseId, teacherId, type);

        printSuccess("Teacher added to course.");
        println(courseService.getDTO(courseId));
    }

    private static void removeTeacherFromCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);

        TeacherType type = UIForms.readLectureOrPractice(scanner);

        Course course = courseService.get(courseId);
        List<Integer> currentTeacherIds = (type == TeacherType.LECTURE)
                ? course.getLectureTeachers()
                : course.getPracticeTeachers();

        if (currentTeacherIds.isEmpty()) {
            printFail("This course has no " + type + " teachers assigned.");
            return;
        }

        println("\n||| Currently assigned " + StringUtils.capitalize(type.toString()) + " teachers |||");
        currentTeacherIds.forEach(id -> println(userService.getDTO(id).toShortString()));

        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);
        courseService.removeTeacher(courseId, teacherId, type);

        printSuccess("Teacher removed from course.");
        println(courseService.getDTO(courseId));
    }

    private static void printTeachersByType(TeacherType type) {
        println("\n||| Available " + type + " teachers |||");
        userService.getAllByClass(Teacher.class).stream()
                .map(user -> (Teacher) user)
                .filter(teacher -> teacher.getType() == type || teacher.getType() == TeacherType.BOTH)
                .forEach(teacher -> println(userService.getDTO(teacher).toShortString()));
    }


    private static void startNewsMenu() {
        ActionMenu menu = new ActionMenu("Manage News");
        menu.addAction("Create news", () -> handleExceptions(() -> createNews()));
        menu.addAction("Delete my news", () -> handleExceptions(() -> deleteOwnNews()));
        menu.addAction("View my news", () -> handleExceptions(() -> printMyNews()));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void createNews() {

        String title = UIForms.readNonEmpty(scanner, "News title: ");
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        NewsUrgencyLevel urgency = UIForms.readNewsUrgencyLevel(scanner);

        News news = new News(getActiveUser().getId(), title, content, urgency);
        newsService.postNews(news);

        printSuccess("News posted.");
        println(newsService.getDTO(news));
    }

    private static void deleteOwnNews() {
        List<News> ownNews = getMyNews();
        if (ownNews.isEmpty()) {
            printFail("You haven't published any news.");
            return;
        }

        println("\n||| Your news |||");
        ownNews.forEach(news -> println(newsService.getDTO(news).toShortString()));

        int newsId = UIForms.readInt(scanner, UIMessage.INPUT_NEWS_ID);
        if (ownNews.stream().noneMatch(news -> news.getId() == newsId)) {
            throw new OperationNotAllowed("deleting news you didn't publish");
        }

        newsService.delete(newsId);
        printSuccess("News deleted.");
    }

    private static void printMyNews() {
        List<News> ownNews = getMyNews();
        if (ownNews.isEmpty()) {
            printFail("You haven't published any news.");
            return;
        }
        println("\n||| Your news |||");
        ownNews.forEach(news -> println(newsService.getDTO(news).toShortString()));
    }

    private static List<News> getMyNews() {
        return newsService.getAll().stream()
                .filter(news -> news.getPublisherId() == getActiveUser().getId())
                .toList();
    }


    private static void startStatisticsMenu() {
        ActionMenu menu = new ActionMenu("Statistics");
        menu.addAction("Get average GPA (Course)", () -> handleExceptions(ManagerApp::averageGpaByCourse));
        menu.addAction("Get average GPA (Student)", () -> handleExceptions(ManagerApp::averageGpaByStudent));
        menu.addAction("Get average GPA (Teacher)", () -> handleExceptions(ManagerApp::averageGpaByTeacher));
        menu.addAction("View all Teachers (Alphabetically)", () -> handleExceptions(ManagerApp::printTeachersAlphabetically));
        menu.addAction("View all Students (Alphabetically)", () -> handleExceptions(ManagerApp::printStudentsAlphabetically));
        menu.addAction("View all Students (By average GPA)", () -> handleExceptions(ManagerApp::printStudentsByGpa));
        menu.addAction("Back", menu::stop);
        menu.start();
    }

    private static void averageGpaByCourse() {
        printAllCourses();
        int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
        Course course = courseService.get(courseId);

        List<Enrollment> enrollments = enrollmentService.getAllByCourseId(courseId);
        Double avg = averageGpa(enrollments);

        if (avg == null) {
            printFail("No enrollments for course '" + course.getName() + "' yet.");
            return;
        }
        println("Average GPA for '" + course.getName() + "' (" + enrollments.size() + " enrollments): " + avg);
    }

    private static void averageGpaByStudent() {
        printAllStudents();
        int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
        User student = userService.get(studentId);

        if (!(student instanceof Student)) {
            throw new OperationNotAllowed("requesting student GPA for non-student user");
        }

        List<Enrollment> enrollments = enrollmentService.getAllByStudentId(studentId);
        Double avg = averageGpa(enrollments);

        if (avg == null) {
            printFail(student.getFullName() + " has no enrollments yet.");
            return;
        }
        println("Average GPA for " + student.getFullName() + " (" + enrollments.size() + " enrollments): " + avg);
    }

    private static void averageGpaByTeacher() {
        printAllTeachers();
        int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);
        User teacher = userService.get(teacherId);

        if (!(teacher instanceof Teacher)) {
            throw new OperationNotAllowed("requesting teacher GPA for non-teacher user");
        }

        List<Enrollment> enrollments = enrollmentService.getAllByTeacherId(teacherId);
        Double avg = averageGpa(enrollments);

        if (avg == null) {
            printFail(teacher.getFullName() + " has no enrollments yet.");
            return;
        }
        println("Average GPA across " + teacher.getFullName() + "'s enrollments (" + enrollments.size() + "): " + avg);
    }

    private static void printTeachersAlphabetically() {
        List<User> teachers = userService.getAllByClass(Teacher.class).stream()
                .sorted(Comparators.USER_BY_FULL_NAME)
                .toList();

        if (teachers.isEmpty()) {
            printFail("No teachers found.");
            return;
        }

        println("\n||| Teachers (alphabetical) |||");
        teachers.forEach(t -> println(userService.getDTO(t).toShortString()));
    }

    private static void printStudentsAlphabetically() {
        List<User> students = userService.getAllByClassOrSubclass(Student.class).stream()
                .sorted(Comparators.USER_BY_FULL_NAME)
                .toList();

        if (students.isEmpty()) {
            printFail("No students found.");
            return;
        }

        println("\n||| Students (alphabetical) |||");
        students.forEach(s -> println(userService.getDTO(s).toShortString()));
    }

    private static void printStudentsByGpa() {
        List<User> students = userService.getAllByClassOrSubclass(Student.class);
        if (students.isEmpty()) {
            printFail("No students found.");
            return;
        }

        students.stream()
                .map(student -> {
                    Double avg = averageGpa(enrollmentService.getAllByStudentId(student.getId()));
                    double safeAvg = avg == null ? 0.0 : avg;
                    return new Object[] { student, safeAvg };
                })
                .sorted(Comparator.comparingDouble((Object[] entry) -> (double) entry[1]).reversed())
                .forEach(entry -> {
                    User student = (User) entry[0];
                    double avg = (double) entry[1];
                    println("GPA: " + avg + " | " + userService.getDTO(student).toShortString());
                });
    }

    private static Double averageGpa(List<Enrollment> enrollments) {
        if (enrollments == null || enrollments.isEmpty()) {
            return null;
        }
        double sum = enrollments.stream().mapToDouble(Enrollment::getGpa).sum();
        double avg = sum / enrollments.size();
        return Math.round(avg * 100.0) / 100.0;
    }

    private static void printAllStudents() {
        println("\n||| Students |||");
        userService.getAllByClassOrSubclass(Student.class)
                .forEach(student -> println(userService.getDTO(student).toShortString()));
    }

    private static void printAllTeachers() {
        println("\n||| Teachers |||");
        userService.getAllByClass(Teacher.class)
                .forEach(teacher -> println(userService.getDTO(teacher).toShortString()));
    }
}

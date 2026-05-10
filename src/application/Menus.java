package application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import exceptions.DoesNotExist;
import model.domain.Admin;
import model.domain.Chat;
import model.domain.Comment;
import model.domain.Course;
import model.domain.Dean;
import model.domain.TeacherComplaint;
import model.domain.Employee;
import model.domain.Enrollment;
import model.domain.Manager;
import model.domain.Message;
import model.domain.News;
import model.domain.GraduateStudent;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.AttestationType;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.CourseType;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.UIMessage;
import model.factories.UserFactory;
import services.ComplaintService;
import services.CourseService;
import services.MessageService;
import services.NewsService;
import services.TeacherService;
import services.UserService;
import settings.AppSettings;
import utils.LogEntry;
import utils.Logger;
import utils.Translator;
import utils.UIForms;

public class Menus extends BaseApp{

    static final UserService userService = services.userService;
    static final CourseService courseService = services.courseService;
    static final TeacherService teacherService = services.teacherService;
    static final ComplaintService complaintService = services.complaintService;
    static final MessageService messageService = services.messageService;
    static final NewsService newsService = services.newsService;

    public static MenuBuilder getMainMenu(){

        User activeUser = getActiveUser();

        MenuBuilder menu = new MenuBuilder("University System v0.0001");
        menu.addAction("My profile", () -> getProfileMenu().start());
        menu.addAction("Research Menu", () -> getResearcherMenu().start());
        menu.addAction("News Menu", () -> getNewsMenu().start());
        menu.addAction("Course Menu", () -> getCourseMenu().start());
        if(activeUser instanceof Employee){
            menu.addAction("Messenger Menu", () -> getMessengerMenu().start());
            menu.addAction("Technical Request Menu", () -> getTechRequestMenu().start());
        }
        if(activeUser instanceof Teacher 
          || activeUser instanceof Dean){
            menu.addAction("Complaint Menu", () -> getComplaintMenu().start());
        }
        if(activeUser instanceof Teacher){
            menu.addAction("Teacher Menu", () -> getTeacherMenu().start());
        }
        if(activeUser instanceof Admin){
            menu.addAction("Admin Menu", () -> getAdminMenu().start());
        }
        if(activeUser instanceof Manager){
            menu.addAction("Manager Menu", () -> getManagerMenu().start());
        }
        if(activeUser instanceof TechSupportSpecialist){
            menu.addAction("Technical Specialist Menu", () -> getTechSupportSpecMenu().start());
        }
        menu.addAction("Exit", () -> menu.stop());
        return menu;
    }

    
    public static MenuBuilder getAuthMenu(){
        MenuBuilder menu = new MenuBuilder("Authentication");
        menu.addAction("Login", () -> {
            login();
            menu.stop();
        });
        menu.addAction("Exit", () -> shutdown());
        return menu;
    }

    private static MenuBuilder getResearcherMenu() {
        MenuBuilder menu = new MenuBuilder("Research Menu");
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static MenuBuilder getNewsMenu() {
        MenuBuilder menu = new MenuBuilder("News Menu");
        for(News news : newsService.getAll()){
            menu.addAction(news.getTitle(), () -> openNews(news));
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void openNews(News news) {
        News updated = newsService.get(news);
        MenuBuilder menu = new MenuBuilder(updated.getTitle());
        menu.addLabel(" -" + news.getContent() + "- ");
        menu.addLabel("Comments: ");
        updated.getComments().forEach(c -> menu.addLabel(c.getSender().getFullname() +  " " + c. getSentDate() + "\n:" + c.getContent()));
        menu.addAction("Leave a comment", () -> {
            leaveComment(updated);
            menu.stop();
        });
        menu.addAction("Back", () -> menu.stop());
        menu.start();
    }


    private static void leaveComment(News news) {
        User activeUser = getActiveUser();
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_COMMENT);
        newsService.assignComment(news, new Comment(activeUser, content));
        openNews(news);
    }


    private static MenuBuilder getCourseMenu() {
        MenuBuilder menu = new MenuBuilder("Course Menu");
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static MenuBuilder getMessengerMenu() {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("Messenger");
        menu.addAction("Start new chat", () -> startChat());
        for(Chat chat : messageService.getAllChats(employee)){
            menu.addAction(chat.getTitleFor(employee), () -> openChat(chat));
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void startChat() {
        Employee activeUser = (Employee) getActiveUser();
        println("||| Employees |||");
        List<Employee> list = userService.getAllByClass(Employee.class);
        list.forEach(u -> println(u));
        int empId = UIForms.readInt(scanner, UIMessage.INPUT_EMPLOYEE_ID);
        Employee emp = userService.get(empId, Employee.class);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        messageService.sendMessage(new Message(activeUser, content), emp);
    }


    private static void openChat(Chat chat) {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("");
        chat.getMessages().forEach(msg -> menu.addLabel(msg.getSender().getFullname() + " " + msg.getSentDate() + "\n" + msg.getContent()));
        menu.addAction("Send Message", ()->{
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
            messageService.sendMessage(new Message(employee, content), chat.getOtherMember(employee));
            openChat(messageService.get(chat));
            menu.stop();
        });
        menu.addAction("Back", () -> menu.stop());
        menu.start();
    }


    private static MenuBuilder getTechRequestMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Request Menu");
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static MenuBuilder getComplaintMenu() {
        MenuBuilder menu = new MenuBuilder("Complaint Menu");
        if(getActiveUser() instanceof Teacher){
            menu.addAction("View my complaints", () -> printTeacherComplaints());
            menu.addAction("Send new complaint", () -> sendComplaint());
        }
        if(getActiveUser() instanceof Dean){
            menu.addAction("View pending complaints", () -> printDeanComplaints());
            menu.addAction("Close pending complaint", () -> closeComplaint());
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }


    private static void sendComplaint() {
            Teacher teacher = (Teacher) getActiveUser();

            List<Dean> deans = userService.getAllByClass(Dean.class);

            println("||| Deans |||");
            deans.forEach(d -> println((Dean) d));
            int deanId = UIForms.readInt(scanner, UIMessage.INPUT_RECEIVER_ID);
            Dean dean = userService.get(deanId, Dean.class);

            println("||| Students |||");
            userService.getAllByClass(Student.class).forEach(st -> println(st));
            int studentId = UIForms.readInt(scanner, UIMessage.INPUT_STUDENT_ID);
            Student student = userService.get(studentId, Student.class);

            ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

            TeacherComplaint complaint = new TeacherComplaint(urgency, teacher, dean, student, content);
            complaintService.sendComplaint(complaint);
            printSuccess("Complaint sent.");
    }

    private static void closeComplaint() {
            Dean dean = (Dean) getActiveUser();
            List<TeacherComplaint> pending = complaintService.getDeanComplaints(dean);
            println("||| Complaints |||");
            pending.forEach(c -> println(c));
            int complaintId = UIForms.readInt(scanner, UIMessage.INPUT_REQUEST_ID);
            TeacherComplaint complaint = complaintService.get(complaintId);
            complaintService.closeComplaint(complaint, dean);
            printSuccess("Complaint closed.");
    }

    private static void printTeacherComplaints() {
        Teacher activeUser = (Teacher) getActiveUser();
        complaintService.getTeacherComplaints(activeUser).forEach(comp -> println(comp));
    }

    private static void printDeanComplaints() {
        Dean activeUser = (Dean) getActiveUser();
        complaintService.getDeanComplaints(activeUser).forEach(comp -> println(comp));
    }


    private static MenuBuilder getTeacherMenu() {
        MenuBuilder menu = new MenuBuilder("Teacher Menu");
        menu.addAction("View my courses", () -> printTeacherCourses());
        menu.addAction("View my students", () -> printTeacherStudents());
        menu.addAction("Put a mark", () -> putMarkToTeacherStudent());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void putMarkToTeacherStudent() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Enrollment>> enrollments = teacherService.getTeacherEnrollments(activeUser);
        List<Enrollment> enrollmentsList = new ArrayList<>();
        println("||| Enrollments |||");
        for(var entry : enrollments.entrySet()){
            println("||| " + entry.getKey().getName() + " |||");
            entry.getValue().forEach(enr -> {
                println(enr);
                enrollmentsList.add(enr);
            });
        }
        int enrollmentId = UIForms.readInt(scanner, UIMessage.INPUT_ENROLLMENT_ID);
        Enrollment enrollment = enrollmentsList.stream().filter(enr -> enr.getId() == enrollmentId)
                                                        .findFirst()
                                                        .orElseThrow(() -> new DoesNotExist("enrollment with id=" + enrollmentId));
        AttestationType type = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        teacherService.putMark(enrollment, activeUser, type, pointsToAdd);
        printSuccess("Mark recorded.");
    }


    private static void printTeacherStudents() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Student>> students = teacherService.getTeacherStudents(activeUser);
        println("||| Students |||");
        for(var entry : students.entrySet()){
            println("||| " + entry.getKey().getName() + " |||");
            entry.getValue().forEach(student -> println(student));
        }
    }


    private static void printTeacherCourses() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<TeacherType, List<Course>> courses = teacherService.getTeacherCourses(activeUser);
        for(var entry : courses.entrySet()){
            println("||| " + entry.getKey().name() + " |||");
            entry.getValue().forEach(course -> println(course));
        }
    }


    private static MenuBuilder getAdminMenu() {
        MenuBuilder menu = new MenuBuilder("Admin Menu");
        menu.addAction("Get all logs", () -> printAllLogs());
        menu.addAction("Get recent logs", () -> printRecentLogs());
        menu.addAction("Get logs by user id", () -> printAllLogsByUserId());
        menu.addAction("Get all users", () -> printAllUsersByClass(User.class));
        menu.addAction("Create User", () -> createUser());
        menu.addAction("Delete User", () -> deleteUser());
        menu.addAction("Ban User", () -> banUser());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void banUser() {
        printAllUsersByClass(User.class);
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        User user = userService.get(userId);
        userService.ban(user);
        printSuccess("User " + user + " is banned");
    }


    private static void deleteUser() {
        printAllUsersByClass(User.class);
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        User user = userService.get(userId);
        userService.delete(user);
        printSuccess("User" + user + "is deleted");
    }


    private static void createUser() {
        retryOnException(() -> {
            Class<? extends User> className = UIForms.readUserClass(scanner);

            String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
            String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
            String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_NAME);
            String surname = UIForms.readNonEmpty(scanner, UIMessage.INPUT_SURNAME);

            Date admissionDate = null;
            TeacherType teacherType = null;

            if (className == Student.class || className == GraduateStudent.class) {
                admissionDate = new Date();
            }
            if (className == Teacher.class) {
                teacherType = UIForms.askTeacherType(scanner);
            }

            User user = UserFactory.createFromClass(className, login, password, name, surname, admissionDate, teacherType);
            User saved = userService.create(user);
            printSuccess(Translator.translate(UIMessage.AUTH_WELCOME, saved.getName()));
        });
    }



    private static <U extends User> void printAllUsersByClass(Class<U> className) {
        List<U> users = userService.getAllByClass(className);
        if(users == null || users.isEmpty()){
            println("No users found.");
            return;
        }
        println("||| " + className.getSimpleName() + " |||");
        users.forEach(BaseApp::println);
    }


    private static void printAllLogsByUserId() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(BaseApp::println);
    }


    private static void printRecentLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println("No recent logs found.");
            return;
        }
        println("Recent logs for last " + AppSettings.RECENT_LOG_HOURS + " hours:");
        logs.forEach(BaseApp::println);
    }


    private static void printAllLogs() {
        List<LogEntry> logs = Logger.getAllLogs();
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(BaseApp::println);
    }


    private static MenuBuilder getManagerMenu() {
        MenuBuilder menu = new MenuBuilder("Manager Menu");
        menu.addAction("View all courses", () -> printAllCourses());
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

    private static void printAllCourses() {
        println("||| Courses |||");
        courseService.getAll().forEach(c -> println(c));
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
            printAllCourses();
            int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
            courseService.delete(courseId);
            printSuccess("Course deleted.");
    }

    private static void assignTeacherToCourse() {
            printAllCourses();
            int courseId = UIForms.readInt(scanner, UIMessage.INPUT_COURSE_ID);
            Course course = courseService.get(courseId);
            println("||| Teachers |||");
            userService.getAllByClass(Teacher.class).forEach(Menus::println);
            int teacherId = UIForms.readInt(scanner, UIMessage.INPUT_TEACHER_ID);
            Teacher teacher = userService.get(teacherId, Teacher.class);
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
        println("||| News |||");
        all.forEach(Menus::println);
    }

    private static void createNews() {
        retryOnException(() -> {
            Manager manager = (Manager) getActiveUser();
            String title = UIForms.readNonEmpty(scanner, "News title: ");
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
            NewsUrgencyLevel urgency = UIForms.readNewsUrgencyLevel(scanner);
            News news = new News(manager, title, content, urgency);
            newsService.postNews(news);
            printSuccess("News posted.");
        });
    }

    private static void deleteNews() {
            Manager manager = (Manager) getActiveUser();
            printAllNews();
            int newsId = UIForms.readInt(scanner, UIMessage.INPUT_NEWS_ID);
            News news = newsService.get(newsId);
            newsService.deleteNews(news, manager);
            printSuccess("News deleted.");
    }

    private static void printStudentGPAStats() {
        List<Student> students = userService.getAllByClass(Student.class);
        if (students.isEmpty()) {
            println("No students.");
            return;
        }
        println("||| Average GPA by student |||");
        int totalGPA = 0;
        int totalStudents = 0;
        for (Student student : students) {
            List<Enrollment> enrollments = courseService.getStudentEnrollments(student);
            if (enrollments.isEmpty()) {
                println(student.getFullname() + " (id=" + student.getId() + "): no enrollments");
                continue;
            }
            double gpa = 0;
            for(var enr : enrollments){
                gpa += enr.getGpa();
            }
            gpa /= enrollments.size();
            totalGPA += gpa;
            totalStudents += 1;
            println(student.getFullname() + " (id=" + student.getId() + "): " + String.format("%.2f", gpa));
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
        println("||| Average enrollment GPA by course |||");
        for (Course course : courses) {
            List<Enrollment> enrollments = course.getEnrollments();
            if (enrollments.isEmpty()) {
                println(course.getName() + " (id=" + course.getId() + "): no enrollments");
                continue;
            }
            double avg = enrollments.stream().mapToDouble(Enrollment::getGpa).average().orElse(0.0);
            println(course.getName() + " (id=" + course.getId() + "): " + String.format("%.2f", avg));
        }
    }

    private static void printTeacherGPAStats() {
        List<Teacher> teachers = userService.getAllByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println("No teachers.");
            return;
        }
        println("||| Average student GPA by teacher |||");
        for (Teacher teacher : teachers) {
            Map<Course, List<Enrollment>> byCourse = teacherService.getTeacherEnrollments(teacher);
            double sum = 0.0;
            int count = 0;
            for (List<Enrollment> enrollments : byCourse.values()) {
                for (Enrollment enr : enrollments) {
                    sum += enr.getGpa();
                    count++;
                }
            }
            if (count == 0) {
                println(teacher.getFullname() + " (id=" + teacher.getId() + "): no enrollments");
            } else {
                println(teacher.getFullname() + " (id=" + teacher.getId() + "): "
                        + String.format("%.2f", sum / count));
            }
        }
    }


    private static MenuBuilder getTechSupportSpecMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Specialist Menu");
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    public static MenuBuilder getProfileMenu() {
        MenuBuilder menu = new MenuBuilder("My profile");
        menu.addAction("View profile", () -> println(getActiveUser().toString()));
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }




    private static void login(){
        String login = UIForms.readNonEmpty(scanner, UIMessage.INPUT_LOGIN);
        String password = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PASSWORD);
        User user = userService.authenticate(login, password);
        printSuccess(Translator.translate(UIMessage.AUTH_WELCOME, user.getName()));
        return;
    }

}

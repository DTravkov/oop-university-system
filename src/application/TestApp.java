package application;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import exceptions.AlreadyExists;
import exceptions.ApplicationException;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;
import model.domain.Comment;
import model.domain.Course;
import model.domain.Dean;
import model.domain.DeletedUser;
import model.domain.Enrollment;
import model.domain.Manager;
import model.domain.Message;
import model.domain.News;
import model.domain.SerializableModel;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.Teacher;
import model.domain.TeacherComplaint;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.CourseType;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.factories.ServiceFactory;
import services.CommentService;
import services.ComplaintService;
import services.CourseService;
import services.EnrollmentService;
import services.MessageService;
import services.NewsService;
import services.StudentOrganizationService;
import services.UserService;

public class TestApp {

    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final UserService userService = serviceFactory.getService(UserService.class);
    private static final CourseService courseService = serviceFactory.getService(CourseService.class);
    private static final EnrollmentService enrollmentService = serviceFactory.getService(EnrollmentService.class);
    private static final CommentService commentService = serviceFactory.getService(CommentService.class);
    private static final MessageService messageService = serviceFactory.getService(MessageService.class);
    private static final ComplaintService complaintService = serviceFactory.getService(ComplaintService.class);
    private static final NewsService newsService = serviceFactory.getService(NewsService.class);
    private static final StudentOrganizationService studentOrganizationService = serviceFactory.getService(StudentOrganizationService.class);

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {
        int passed = 0;
        int total = 17;

        passed += runTest("User create", TestApp::testUserCreate) ? 1 : 0;
        passed += runTest("User login", TestApp::testUserLogin) ? 1 : 0;
        passed += runTest("User delete", TestApp::testUserDelete) ? 1 : 0;
        passed += runTest("Course on teacher delete", TestApp::testCourseOnTeacherDelete) ? 1 : 0;
        passed += runTest("Enrollment on student delete", TestApp::testEnrollmentOnStudentDelete) ? 1 : 0;
        passed += runTest("News on manager delete", TestApp::testNewsOnManagerDelete) ? 1 : 0;
        passed += runTest("Non manager news create", TestApp::testNonManagerNewsCreate) ? 1 : 0;
        passed += runTest("Non student enrollment", TestApp::testNonStudentEnrollment) ? 1 : 0;
        passed += runTest("Non employee message", TestApp::testNonEmployeeMessage) ? 1 : 0;
        passed += runTest("Duplicate user login", TestApp::testDuplicateUserLogin) ? 1 : 0;
        passed += runTest("Duplicate enrollment", TestApp::testDuplicateEnrollment) ? 1 : 0;
        passed += runTest("Teacher role mismatch in course assignment", TestApp::testTeacherRoleMismatchInCourseAssignment) ? 1 : 0;
        passed += runTest("Message on user delete", TestApp::testMessageOnUserDelete) ? 1 : 0;
        passed += runTest("Complaint role guards", TestApp::testComplaintRoleGuards) ? 1 : 0;
        passed += runTest("Organization member must be student", TestApp::testOrganizationMemberMustBeStudent) ? 1 : 0;
        passed += runTest("News comment create", TestApp::testNewsCommentCreate) ? 1 : 0;
        passed += runTest("News on comment delete", TestApp::testNewsOnCommentDelete) ? 1 : 0;

        System.out.println("\n=== TEST RESULT ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Failed: " + (total - passed) + "/" + total);
    }

    public static void runScenario() {
        runAllTests();
    }

    public static void printAllData() {
        System.out.println("=== Persisted Data Snapshot ===");
        printModelList("Students", userService.getAllByClass(Student.class));
        printModelList("Courses", courseService.getAll());
        printModelList("Enrollments", enrollmentService.getAll());
        printModelList("Teachers", userService.getAllByClass(Teacher.class));
        printModelList("Deans", userService.getAllByClass(Dean.class));
        printModelList("Managers", userService.getAllByClass(Manager.class));
        printModelList("Messages", messageService.getAll());
        printModelList("Complaints", complaintService.getAll());
        printModelList("Organizations", studentOrganizationService.getAll());
        printModelList("News", newsService.getAll());
    }

    private static List<String> getFormattedList(List<? extends SerializableModel> list) {
        return list.stream().map(model -> model.toString() + '\n').toList();
    }

    private static void printModelList(String modelName, List<? extends SerializableModel> list) {
        System.out.println("--- " + modelName + " ---");
        System.out.print(String.join("", getFormattedList(list)));
        System.out.println();
    }

    // basic CRUD tests
    public static boolean testUserCreate() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.user.create." + suffix, "12345", "Test", "Create", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Student loaded = (Student) userService.get(student.getId());
            return loaded.getId() == student.getId();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testUserLogin() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Manager manager = (Manager) userService.registerUser(
                    Manager.class, "tc.user.login." + suffix, "pass-login", "Test", "Login", null, null
            );
            cleanupBin.trackUser(manager.getId());
            return userService.authenticate("tc.user.login." + suffix, "pass-login").getId() == manager.getId();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testUserDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.user.delete." + suffix, "12345", "Test", "Delete", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            int teacherId = teacher.getId();
            userService.delete(teacherId);
            return expectThrows(DoesNotExist.class, () -> userService.get(teacherId));
        } finally {
            cleanupBin.cleanup();
        }
    }

    // specific business logic tests
    public static boolean testCourseOnTeacherDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.course.teacher." + suffix, "12345", "Course", "Teacher", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            Course course = courseService.create(new Course("tc-course-" + suffix, "course delete relation", 3, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), teacher.getId(), TeacherType.LECTURE);
            userService.delete(teacher.getId());
            Course updatedCourse = courseService.get(course.getId());
            return !updatedCourse.getLectureTeachers().contains(teacher.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentOnStudentDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Course course = courseService.create(new Course("tc-enr-course-" + suffix, "enrollment relation", 4, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            Enrollment enrollment = enrollmentService.create(new Enrollment(course.getId(), student.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            userService.delete(student.getId());
            return enrollmentService.getAllByStudentId(student.getId()).isEmpty();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testNewsOnManagerDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Manager manager = (Manager) userService.registerUser(
                    Manager.class, "tc.news.manager." + suffix, "12345", "News", "Manager", null, null
            );
            cleanupBin.trackUser(manager.getId());
            News news = new News(manager.getId(), "tc-news-" + suffix, "manager delete relation", NewsUrgencyLevel.HIGH);
            newsService.postNews(news);
            cleanupBin.trackNews(news.getId());
            int newsId = news.getId();
            userService.delete(manager.getId());
            return newsService.get(newsId).getPublisherId() == DeletedUser.ID;
        } finally {
            cleanupBin.cleanup();
        }
    }

    // role violation tests
    public static boolean testNonManagerNewsCreate() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonmanager.news." + suffix, "12345", "Wrong", "Publisher", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(teacher.getId());
            News invalidNews = new News(teacher.getId(), "tc-invalid-news-" + suffix, "should fail", NewsUrgencyLevel.LOW);
            return expectThrows(OperationNotAllowed.class, () -> newsService.postNews(invalidNews));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testNonStudentEnrollment() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonenroll.teacher." + suffix, "12345", "Wrong", "Enrollment", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            Course course = courseService.create(new Course("tc-non-student-course-" + suffix, "should fail", 2, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            return expectThrows(OperationNotAllowed.class, () -> enrollmentService.create(new Enrollment(course.getId(), teacher.getId())));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testNonEmployeeMessage() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.nonemployee.sender." + suffix, "12345", "Wrong", "Sender", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonemployee.receiver." + suffix, "12345", "Valid", "Receiver", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    messageService.sendMessage(new Message(student.getId(), teacher.getId(), "should not send")));
        } finally {
            cleanupBin.cleanup();
        }
    }

    // tier 2 tests
    public static boolean testDuplicateUserLogin() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        String login = "tc.dup.user." + suffix;
        try {
            Student first = (Student) userService.registerUser(Student.class, login, "12345", "Dup", "One", new Date(), null);
            cleanupBin.trackUser(first.getId());
            return expectThrows(AlreadyExists.class, () ->
                    userService.registerUser(Student.class, login, "12345", "Dup", "Two", new Date(), null));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testDuplicateEnrollment() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.dup.enr.student." + suffix, "12345", "Dup", "Enroll", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Course course = courseService.create(new Course("tc-dup-enr-course-" + suffix, "duplicate enrollment", 4, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            Enrollment enrollment = enrollmentService.create(new Enrollment(course.getId(), student.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            return expectThrows(AlreadyExists.class, () ->
                    enrollmentService.create(new Enrollment(course.getId(), student.getId())));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTeacherRoleMismatchInCourseAssignment() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher lectureOnly = (Teacher) userService.registerUser(
                    Teacher.class, "tc.role.mismatch.teacher." + suffix, "12345", "Role", "Mismatch", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureOnly.getId());
            Course course = courseService.create(new Course("tc-role-mismatch-course-" + suffix, "role mismatch", 3, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    courseService.addTeacher(course.getId(), lectureOnly.getId(), TeacherType.PRACTICE));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testMessageOnUserDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.msg.delete.sender." + suffix, "12345", "Msg", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            Dean receiver = (Dean) userService.registerUser(
                    Dean.class, "tc.msg.delete.receiver." + suffix, "12345", "Msg", "Receiver", null, null
            );
            cleanupBin.trackUser(receiver.getId());
            Message message = new Message(sender.getId(), receiver.getId(), "delete user message mapping");
            messageService.sendMessage(message);
            cleanupBin.trackMessage(message.getId());
            userService.delete(sender.getId());
            List<Message> messagesByReceiver = messageService.getAllByReceiverId(receiver.getId());
            return messagesByReceiver.stream().anyMatch(msg ->
                    msg.getContent().equals("delete user message mapping") && msg.getSenderId() == DeletedUser.ID);
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testComplaintRoleGuards() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student studentSender = (Student) userService.registerUser(
                    Student.class, "tc.comp.sender." + suffix, "12345", "Comp", "Sender", new Date(), null
            );
            cleanupBin.trackUser(studentSender.getId());
            Dean deanReceiver = (Dean) userService.registerUser(
                    Dean.class, "tc.comp.dean." + suffix, "12345", "Comp", "Dean", null, null
            );
            cleanupBin.trackUser(deanReceiver.getId());
            Student aboutStudent = (Student) userService.registerUser(
                    Student.class, "tc.comp.about." + suffix, "12345", "Comp", "About", new Date(), null
            );
            cleanupBin.trackUser(aboutStudent.getId());

            TeacherComplaint invalidComplaint = new TeacherComplaint(
                    ComplaintUrgencyLevel.HIGH,
                    studentSender.getId(),
                    deanReceiver.getId(),
                    aboutStudent.getId(),
                    "invalid sender role"
            );
            return expectThrows(OperationNotAllowed.class, () -> complaintService.sendComplaint(invalidComplaint));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testOrganizationMemberMustBeStudent() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student president = (Student) userService.registerUser(
                    Student.class, "tc.org.president." + suffix, "12345", "Org", "President", new Date(), null
            );
            cleanupBin.trackUser(president.getId());
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.org.teacher." + suffix, "12345", "Org", "Teacher", null, TeacherType.BOTH
            );
            cleanupBin.trackUser(teacher.getId());
            StudentOrganization organization = studentOrganizationService.create(
                    new StudentOrganization("tc-org-" + suffix, "member role guard", president.getId())
            );
            cleanupBin.trackOrganization(organization.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    studentOrganizationService.addMember(organization.getId(), teacher.getId()));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testNewsCommentCreate() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Manager manager = (Manager) userService.registerUser(
                    Manager.class, "tc.news.comment.manager." + suffix, "12345", "News", "Manager", null, null
            );
            cleanupBin.trackUser(manager.getId());
            Teacher commenter = (Teacher) userService.registerUser(
                    Teacher.class, "tc.news.comment.teacher." + suffix, "12345", "News", "Commenter", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(commenter.getId());

            News news = new News(manager.getId(), "tc-news-comment-" + suffix, "comment assign check", NewsUrgencyLevel.AVERAGE);
            newsService.postNews(news);
            cleanupBin.trackNews(news.getId());

            Comment comment = commentService.create(new Comment(commenter.getId(), "test news comment"));
            cleanupBin.trackComment(comment.getId());

            newsService.assignComment(news.getId(), comment.getId());
            return newsService.get(news.getId()).getComments().contains(comment.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testNewsOnCommentDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Manager manager = (Manager) userService.registerUser(
                    Manager.class, "tc.news.comment.delete.manager." + suffix, "12345", "News", "Manager", null, null
            );
            cleanupBin.trackUser(manager.getId());
            Teacher commenter = (Teacher) userService.registerUser(
                    Teacher.class, "tc.news.comment.delete.teacher." + suffix, "12345", "News", "Commenter", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(commenter.getId());

            News news = new News(manager.getId(), "tc-news-comment-delete-" + suffix, "comment delete check", NewsUrgencyLevel.HIGH);
            newsService.postNews(news);
            cleanupBin.trackNews(news.getId());

            Comment comment = commentService.create(new Comment(commenter.getId(), "comment to delete"));
            cleanupBin.trackComment(comment.getId());
            newsService.assignComment(news.getId(), comment.getId());

            commentService.delete(comment.getId());
            return !newsService.get(news.getId()).getComments().contains(comment.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    private static boolean runTest(String name, TestCase testCase) {
        try {
            boolean result = testCase.run();
            System.out.println((result ? "[PASS] " : "[FAIL] ") + name);
            return result;
        } catch (ApplicationException e) {
            System.out.println("[FAIL] " + name + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("[FAIL] " + name + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean expectThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
            return false;
        } catch (Throwable t) {
            return expectedType.isInstance(t);
        }
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @FunctionalInterface
    private interface TestCase {
        boolean run();
    }

    private static class CleanupBin {
        private final List<Integer> userIds = new java.util.ArrayList<>();
        private final List<Integer> courseIds = new java.util.ArrayList<>();
        private final List<Integer> enrollmentIds = new java.util.ArrayList<>();
        private final List<Integer> commentIds = new java.util.ArrayList<>();
        private final List<Integer> messageIds = new java.util.ArrayList<>();
        private final List<Integer> complaintIds = new java.util.ArrayList<>();
        private final List<Integer> newsIds = new java.util.ArrayList<>();
        private final List<Integer> organizationIds = new java.util.ArrayList<>();

        void trackUser(int id) { userIds.add(id); }
        void trackCourse(int id) { courseIds.add(id); }
        void trackEnrollment(int id) { enrollmentIds.add(id); }
        void trackComment(int id) { commentIds.add(id); }
        void trackMessage(int id) { messageIds.add(id); }
        void trackNews(int id) { newsIds.add(id); }
        void trackOrganization(int id) { organizationIds.add(id); }

        void cleanup() {
            deleteAll(organizationIds, id -> studentOrganizationService.delete(id));
            deleteAll(complaintIds, id -> complaintService.delete(id));
            deleteAll(messageIds, id -> messageService.delete(id));
            deleteAll(commentIds, id -> commentService.delete(id));
            deleteAll(enrollmentIds, id -> enrollmentService.delete(id));
            deleteAll(newsIds, id -> newsService.delete(id));
            deleteAll(courseIds, id -> courseService.delete(id));
            deleteAll(userIds, id -> userService.delete(id));
        }

        private void deleteAll(List<Integer> ids, java.util.function.IntConsumer deleteAction) {
            for (int i = ids.size() - 1; i >= 0; i--) {
                try {
                    deleteAction.accept(ids.get(i));
                } catch (Exception ignored) {
                }
            }
        }
    }

}

package application;

import java.util.Date;
import java.util.List;
import java.util.UUID;


import exceptions.*;
import model.domain.*;
import services.*;
import settings.*;
import utils.*;
import model.enumeration.*;

public class TestApp extends BaseApp {

    private static final UserService userService = services.userService;
    private static final CourseService courseService = services.courseService;
    private static final EnrollmentService enrollmentService = services.enrollmentService;
    private static final CommentService commentService = services.commentService;
    private static final MessageService messageService = services.messageService;
    private static final ComplaintService complaintService = services.complaintService;
    private static final NewsService newsService = services.newsService;
    private static final StudentOrganizationService studentOrganizationService =
            services.studentOrganizationService;
    private static final ResearchService researchService = services.researchService;
    private static final TechRequestService techRequestService = services.techRequestService;

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {

        Logger.setIsActive(false);
        
        List<TestEntry> tests = List.of(
                new TestEntry("User create", TestApp::testUserCreate),
                new TestEntry("User login", TestApp::testUserLogin),
                new TestEntry("User delete", TestApp::testUserDelete),
                new TestEntry("Course updates on teacher delete", TestApp::testCourseOnTeacherDelete),
                new TestEntry("Enrollment removed on student delete", TestApp::testEnrollmentOnStudentDelete),
                new TestEntry("News publisher replaced on manager delete", TestApp::testNewsOnManagerDelete),
                new TestEntry("News requires manager publisher", TestApp::testNonManagerNewsCreate),
                new TestEntry("Enrollment requires student", TestApp::testNonStudentEnrollment),
                new TestEntry("Message requires employee accounts", TestApp::testNonEmployeeMessage),
                new TestEntry("Duplicate user login", TestApp::testDuplicateUserLogin),
                new TestEntry("Duplicate enrollment", TestApp::testDuplicateEnrollment),
                new TestEntry("Enrollment removed on course delete", TestApp::testEnrollmentRemovedOnCourseDelete),
                new TestEntry("Enrollment rejects lecture teacher not on course", TestApp::testEnrollmentLectureTeacherNotOnCourse),
                new TestEntry("Enrollment rejects practice teacher not on course", TestApp::testEnrollmentPracticeTeacherNotOnCourse),
                new TestEntry("Enrollment lecture teacher replaced on teacher delete", TestApp::testEnrollmentLectureTeacherReplacedOnTeacherDelete),
                new TestEntry("Enrollment practice teacher replaced on teacher delete", TestApp::testEnrollmentPracticeTeacherReplacedOnTeacherDelete),
                new TestEntry("Enrollment increasePoints rejects invalid type", TestApp::testEnrollmentIncreasePointsInvalidType),
                new TestEntry("Course rejects teacher type mismatch", TestApp::testTeacherRoleMismatchInCourseAssignment),
                new TestEntry("Message sender replaced on user delete", TestApp::testMessageOnUserDelete),
                new TestEntry("Tech request send success", TestApp::testTechRequestSendSuccess),
                new TestEntry("Tech request rejects self-addressed", TestApp::testTechRequestRejectsSelfAddressed),
                new TestEntry("Tech request rejects student sender", TestApp::testTechRequestRejectsStudentSender),
                new TestEntry("Tech request rejects non-specialist receiver", TestApp::testTechRequestRejectsNonSpecialistReceiver),
                new TestEntry("Tech request sender replaced on user delete", TestApp::testTechRequestSenderReplacedOnUserDelete),
                new TestEntry("Tech request receiver DONE unchanged on specialist delete", TestApp::testTechRequestReceiverDoneUnchangedOnDelete),
                new TestEntry("Tech request receiver not DONE resets to pending on specialist delete", TestApp::testTechRequestReceiverNotDoneResetsOnDelete),
                new TestEntry("Complaint role guards", TestApp::testComplaintRoleGuards),
                new TestEntry("Organization members must be students", TestApp::testOrganizationMemberMustBeStudent),
                new TestEntry("Student org duplicate name", TestApp::testStudentOrganizationDuplicateName),
                new TestEntry("Student org president role constraint", TestApp::testStudentOrganizationPresidentRoleConstraint),
                new TestEntry("Student org president deleted", TestApp::testStudentOrganizationPresidentDeleted),
                new TestEntry("News comment assignment", TestApp::testNewsCommentCreate),
                new TestEntry("News removes deleted comment", TestApp::testNewsOnCommentDelete),
                new TestEntry("Research profile create", TestApp::testResearchProfileManualCreateFlow),
                new TestEntry("Research profile duplicate create", TestApp::testResearchProfileDuplicateCreateGuard),
                new TestEntry("Research profile auto create", TestApp::testResearchProfileAutoCreateOnGraduateCreate),
                new TestEntry("Research profile delete", TestApp::testResearchProfileManualDeleteFlow),
                new TestEntry("Research profile auto delete", TestApp::testResearchProfileAutoDeleteOnUserDelete)
        );

        int passed = 0;
        for (TestEntry test : tests) {
            if (runTest(test.name, test.testCase)) {
                passed++;
            }
        }

        int total = tests.size();
        println("\n=== TEST RESULT ===");
        println("Passed: " + passed + "/" + total);
        println("Failed: " + (total - passed) + "/" + total);


        Logger.setIsActive(true);


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
            User authenticated = userService.authenticate("tc.user.login." + suffix, "pass-login");
            return authenticated.getId() == manager.getId() && authenticated.getId() == AppSettings.getActiveUser().getId();
            
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
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.lecture." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.practice." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-course-" + suffix, "enrollment relation", 4, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
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
            return newsService.get(newsId).getPublisherId() == AppSettings.DELETED_USER_ID;
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
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonenroll.lecture." + suffix, "12345", "Course", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonenroll.practice." + suffix, "12345", "Course", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.nonenroll.teacher." + suffix, "12345", "Wrong", "Enrollment", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            Course course = courseService.create(new Course("tc-non-student-course-" + suffix, "should fail", 2, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            return expectThrows(OperationNotAllowed.class, () -> enrollmentService.create(new Enrollment(
                    course.getId(), teacher.getId(), lectureTeacher.getId(), practiceTeacher.getId())));
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
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.dup.enr.lecture." + suffix, "12345", "Dup", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.dup.enr.practice." + suffix, "12345", "Dup", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-dup-enr-course-" + suffix, "duplicate enrollment", 4, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            return expectThrows(AlreadyExists.class, () ->
                    enrollmentService.create(new Enrollment(
                            course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId())));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentRemovedOnCourseDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.del.course.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.del.course.lecture." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.del.course.practice." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-del-course-" + suffix, "course delete drops enrollments", 3, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
            int enrollmentId = enrollment.getId();
            cleanupBin.trackEnrollment(enrollmentId);
            courseService.delete(course.getId());
            cleanupBin.untrackCourse(course.getId());
            return expectThrows(DoesNotExist.class, () -> enrollmentService.get(enrollmentId));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentLectureTeacherNotOnCourse() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.badlect.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher onCourseLecture = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badlect.on." + suffix, "12345", "On", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(onCourseLecture.getId());
            Teacher notOnCourseLecture = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badlect.off." + suffix, "12345", "Off", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(notOnCourseLecture.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badlect.practice." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-bad-lect-" + suffix, "lecture must teach course", 2, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), onCourseLecture.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            return expectThrows(DoesNotExist.class, () -> enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), notOnCourseLecture.getId(), practiceTeacher.getId())));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentPracticeTeacherNotOnCourse() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.badpr.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badpr.lecture." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher onCoursePractice = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badpr.on." + suffix, "12345", "On", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(onCoursePractice.getId());
            Teacher notOnCoursePractice = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.badpr.off." + suffix, "12345", "Off", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(notOnCoursePractice.getId());
            Course course = courseService.create(new Course("tc-enr-bad-pr-" + suffix, "practice must teach course", 2, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), onCoursePractice.getId(), TeacherType.PRACTICE);
            return expectThrows(DoesNotExist.class, () -> enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), notOnCoursePractice.getId())));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentLectureTeacherReplacedOnTeacherDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.tdel.lect.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.tdel.lect." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.tdel.practice." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-tdel-lect-" + suffix, "lecture teacher delete", 3, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            int lectureId = lectureTeacher.getId();
            userService.delete(lectureId);
            cleanupBin.untrackUser(lectureId);
            Enrollment updated = enrollmentService.get(enrollment.getId());
            return updated.getLectureTeacherId() == AppSettings.DELETED_USER_ID
                    && updated.getPracticeTeacherId() == practiceTeacher.getId();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentPracticeTeacherReplacedOnTeacherDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.tdel.pr.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.tdel.pr.lecture." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.tdel.pr." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-tdel-pr-" + suffix, "practice teacher delete", 3, CourseType.MAJOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            int practiceId = practiceTeacher.getId();
            userService.delete(practiceId);
            cleanupBin.untrackUser(practiceId);
            Enrollment updated = enrollmentService.get(enrollment.getId());
            return updated.getPracticeTeacherId() == AppSettings.DELETED_USER_ID
                    && updated.getLectureTeacherId() == lectureTeacher.getId();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testEnrollmentIncreasePointsInvalidType() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.enr.points.student." + suffix, "12345", "Enroll", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            Teacher lectureTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.points.lecture." + suffix, "12345", "Enroll", "Lecture", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(lectureTeacher.getId());
            Teacher practiceTeacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.enr.points.practice." + suffix, "12345", "Enroll", "Practice", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(practiceTeacher.getId());
            Course course = courseService.create(new Course("tc-enr-points-" + suffix, "invalid point branch", 2, CourseType.MINOR));
            cleanupBin.trackCourse(course.getId());
            courseService.addTeacher(course.getId(), lectureTeacher.getId(), TeacherType.LECTURE);
            courseService.addTeacher(course.getId(), practiceTeacher.getId(), TeacherType.PRACTICE);
            Enrollment enrollment = enrollmentService.create(new Enrollment(
                    course.getId(), student.getId(), lectureTeacher.getId(), practiceTeacher.getId()));
            cleanupBin.trackEnrollment(enrollment.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    enrollmentService.increasePoints(enrollment.getId(), 0, 1.0));
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
                    msg.getContent().equals("delete user message mapping") && msg.getSenderId() == AppSettings.DELETED_USER_ID);
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestSendSuccess() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.ok.sender." + suffix, "12345", "Tech", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.ok.spec." + suffix, "12345", "Tech", "Spec", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            TechRequest created = techRequestService.sendRequest(
                    new TechRequest(sender.getId(), specialist.getId(), "projector issue"));
            cleanupBin.trackTechRequest(created.getId());
            TechRequest loaded = techRequestService.get(created.getId());
            return loaded.getSenderId() == sender.getId()
                    && loaded.getReceiverId() == specialist.getId()
                    && loaded.getStatus() == TechRequestStatus.PENDING;
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestRejectsSelfAddressed() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.self." + suffix, "12345", "Tech", "Self", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            int id = specialist.getId();
            return expectThrows(OperationNotAllowed.class, () ->
                    techRequestService.sendRequest(new TechRequest(id, id, "to myself")));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestRejectsStudentSender() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.tech.student." + suffix, "12345", "Tech", "Student", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.student.spec." + suffix, "12345", "Tech", "Spec", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    techRequestService.sendRequest(new TechRequest(student.getId(), specialist.getId(), "help")));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestRejectsNonSpecialistReceiver() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.ns.sender." + suffix, "12345", "Tech", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            Teacher receiver = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.ns.recv." + suffix, "12345", "Tech", "Receiver", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(receiver.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    techRequestService.sendRequest(new TechRequest(sender.getId(), receiver.getId(), "wrong receiver")));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestSenderReplacedOnUserDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.del.sender." + suffix, "12345", "Tech", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.del.spec." + suffix, "12345", "Tech", "Spec", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            TechRequest request = techRequestService.sendRequest(
                    new TechRequest(sender.getId(), specialist.getId(), "replace sender"));
            cleanupBin.trackTechRequest(request.getId());
            int senderId = sender.getId();
            userService.delete(senderId);
            cleanupBin.untrackUser(senderId);
            TechRequest updated = techRequestService.get(request.getId());
            return updated.getSenderId() == AppSettings.DELETED_USER_ID
                    && updated.getReceiverId() == specialist.getId();
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestReceiverDoneUnchangedOnDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.done.sender." + suffix, "12345", "Tech", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.done.spec." + suffix, "12345", "Tech", "Spec", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            TechRequest request = techRequestService.sendRequest(
                    new TechRequest(sender.getId(), specialist.getId(), "done flow"));
            cleanupBin.trackTechRequest(request.getId());
            request.setStatus(TechRequestStatus.DONE);
            techRequestService.updateRequest(request);
            int specialistId = specialist.getId();
            userService.delete(specialistId);
            cleanupBin.untrackUser(specialistId);
            TechRequest updated = techRequestService.get(request.getId());
            return updated.getStatus() == TechRequestStatus.DONE
                    && updated.getReceiverId() == AppSettings.DELETED_USER_ID;
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testTechRequestReceiverNotDoneResetsOnDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher sender = (Teacher) userService.registerUser(
                    Teacher.class, "tc.tech.reset.sender." + suffix, "12345", "Tech", "Sender", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(sender.getId());
            TechSupportSpecialist specialist = (TechSupportSpecialist) userService.registerUser(
                    TechSupportSpecialist.class, "tc.tech.reset.spec." + suffix, "12345", "Tech", "Spec", null, null
            );
            cleanupBin.trackUser(specialist.getId());
            TechRequest request = techRequestService.sendRequest(
                    new TechRequest(sender.getId(), specialist.getId(), "accepted then specialist leaves"));
            cleanupBin.trackTechRequest(request.getId());
            request.setStatus(TechRequestStatus.ACCEPTED);
            techRequestService.updateRequest(request);
            int specialistId = specialist.getId();
            userService.delete(specialistId);
            cleanupBin.untrackUser(specialistId);
            TechRequest updated = techRequestService.get(request.getId());
            return updated.getStatus() == TechRequestStatus.PENDING
                    && updated.getReceiverId() == AppSettings.DELETED_USER_ID;
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

    public static boolean testStudentOrganizationDuplicateName() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        String orgName = "tc-sorg-dup-" + suffix;
        try {
            Student president = (Student) userService.registerUser(
                    Student.class, "tc.sorg.dup.p1." + suffix, "12345", "Sorg", "P1", new Date(), null
            );
            cleanupBin.trackUser(president.getId());
            Student other = (Student) userService.registerUser(
                    Student.class, "tc.sorg.dup.p2." + suffix, "12345", "Sorg", "P2", new Date(), null
            );
            cleanupBin.trackUser(other.getId());
            StudentOrganization first = studentOrganizationService.create(
                    new StudentOrganization(orgName, "first", president.getId())
            );
            cleanupBin.trackOrganization(first.getId());
            return expectThrows(AlreadyExists.class, () -> studentOrganizationService.create(
                    new StudentOrganization(orgName, "second", other.getId())
            ));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testStudentOrganizationPresidentRoleConstraint() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student president = (Student) userService.registerUser(
                    Student.class, "tc.sorg.spt.p." + suffix, "12345", "Sorg", "Pres", new Date(), null
            );
            cleanupBin.trackUser(president.getId());
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.sorg.spt.t." + suffix, "12345", "Sorg", "Teacher", null, TeacherType.LECTURE
            );
            cleanupBin.trackUser(teacher.getId());
            StudentOrganization org = studentOrganizationService.create(
                    new StudentOrganization("tc-sorg-spt-" + suffix, "teacher president", president.getId())
            );
            cleanupBin.trackOrganization(org.getId());
            return expectThrows(OperationNotAllowed.class, () ->
                    studentOrganizationService.setPresident(org.getId(), teacher.getId()));
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testStudentOrganizationPresidentDeleted() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student president = (Student) userService.registerUser(
                    Student.class, "tc.sorg.delp." + suffix, "12345", "Sorg", "Pres", new Date(), null
            );
            cleanupBin.trackUser(president.getId());
            StudentOrganization org = studentOrganizationService.create(
                    new StudentOrganization("tc-sorg-delp-" + suffix, "president deleted", president.getId())
            );
            cleanupBin.trackOrganization(org.getId());
            int presidentId = president.getId();
            userService.delete(presidentId);
            cleanupBin.untrackUser(presidentId);
            StudentOrganization updated = studentOrganizationService.get(org.getId());
            return updated.getPresidentId() == AppSettings.DELETED_USER_ID;
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

    public static boolean testResearchProfileManualCreateFlow() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.research.manual." + suffix, "12345", "Research", "Manual", new Date(), null
            );
            cleanupBin.trackUser(student.getId());

            if (researchService.isResearcher(student.getId())) {
                return false;
            }

            ResearcherProfile profile = researchService.makeResearcher(student.getId());
            return profile.getUserId() == student.getId() && researchService.isResearcher(student.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testResearchProfileDuplicateCreateGuard() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.research.dup." + suffix, "12345", "Research", "Dup", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            researchService.makeResearcher(student.getId());

            try {
                researchService.makeResearcher(student.getId());
                return false;
            } catch (AlreadyExists e) {
                return true;
            }
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testResearchProfileAutoCreateOnGraduateCreate() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student graduateStudent = (Student) userService.registerUser(
                    model.domain.GraduateStudent.class, "tc.research.event.create." + suffix, "12345", "Research", "Event", new Date(), null
            );
            cleanupBin.trackUser(graduateStudent.getId());
            return researchService.isResearcher(graduateStudent.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testResearchProfileManualDeleteFlow() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Student student = (Student) userService.registerUser(
                    Student.class, "tc.research.delete." + suffix, "12345", "Research", "Delete", new Date(), null
            );
            cleanupBin.trackUser(student.getId());
            researchService.makeResearcher(student.getId());
            researchService.deleteResearcherProfile(student.getId());

            if (researchService.isResearcher(student.getId())) {
                return false;
            }

            try {
                researchService.getResearcherProfile(student.getId());
                return false;
            } catch (DoesNotExist e) {
                return true;
            }
        } finally {
            cleanupBin.cleanup();
        }
    }

    public static boolean testResearchProfileAutoDeleteOnUserDelete() {
        CleanupBin cleanupBin = new CleanupBin();
        String suffix = uniqueSuffix();
        try {
            Teacher teacher = (Teacher) userService.registerUser(
                    Teacher.class, "tc.research.event.delete." + suffix, "12345", "Research", "DeleteEvent", null, TeacherType.PRACTICE
            );
            cleanupBin.trackUser(teacher.getId());

            if (!researchService.isResearcher(teacher.getId())) {
                return false;
            }

            userService.delete(teacher.getId());
            return !researchService.isResearcher(teacher.getId());
        } finally {
            cleanupBin.cleanup();
        }
    }

    private static boolean runTest(String name, TestCase testCase) {
        try {
            boolean result = testCase.run();
            println((result ? "[PASS] " : "[FAIL] ") + name);
            return result;
        } catch (ApplicationException e) {
            print("[FAIL] " + name + " -> " + e.getClass().getSimpleName() + ": ");
            printExceptionDetails(e);
            return false;
        } catch (Exception e) {
            println("[FAIL] " + name + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
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

    private static class TestEntry {
        private final String name;
        private final TestCase testCase;

        private TestEntry(String name, TestCase testCase) {
            this.name = name;
            this.testCase = testCase;
        }
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
        private final List<Integer> techRequestIds = new java.util.ArrayList<>();

        void trackUser(int id) { userIds.add(id); }
        void untrackUser(int id) { userIds.remove(Integer.valueOf(id)); }
        void trackCourse(int id) { courseIds.add(id); }
        void untrackCourse(int id) { courseIds.remove(Integer.valueOf(id)); }
        void trackEnrollment(int id) { enrollmentIds.add(id); }
        void trackComment(int id) { commentIds.add(id); }
        void trackMessage(int id) { messageIds.add(id); }
        void trackNews(int id) { newsIds.add(id); }
        void trackOrganization(int id) { organizationIds.add(id); }
        void trackTechRequest(int id) { techRequestIds.add(id); }

        void cleanup() {
            deleteAll(organizationIds, id -> studentOrganizationService.delete(id));
            deleteAll(complaintIds, id -> complaintService.delete(id));
            deleteAll(messageIds, id -> messageService.delete(id));
            deleteAll(commentIds, id -> commentService.delete(id));
            deleteAll(enrollmentIds, id -> enrollmentService.delete(id));
            deleteAll(newsIds, id -> newsService.delete(id));
            deleteAll(techRequestIds, id -> techRequestService.delete(id));
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

package application;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import model.domain.ResearchPaper;
import model.domain.ResearcherProfile;
import model.domain.Student;
import model.domain.StudentOrganization;
import model.domain.Teacher;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.AttestationType;
import model.enumeration.ComplaintUrgencyLevel;
import model.enumeration.CourseType;
import model.enumeration.LanguagePreference;
import model.enumeration.NewsUrgencyLevel;
import model.enumeration.TeacherType;
import model.enumeration.TechRequestStatus;
import model.enumeration.UIMessage;
import model.factories.UserFactory;
import services.ComplaintService;
import services.CourseService;
import services.EnrollmentService;
import services.MessageService;
import services.NewsService;
import services.ResearchService;
import services.StudentOrganizationService;
import services.TeacherService;
import services.TechRequestService;
import services.UserService;
import settings.AppSettings;
import utils.Comparators;
import utils.LogEntry;
import utils.Logger;
import utils.StringUtils;
import utils.Translator;
import utils.UIForms;

import java.util.Comparator;

public class Menus extends BaseApp{

    static final UserService userService = services.userService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final CourseService courseService = services.courseService;
    static final TeacherService teacherService = services.teacherService;
    static final ComplaintService complaintService = services.complaintService;
    static final MessageService messageService = services.messageService;
    static final NewsService newsService = services.newsService;
    static final TechRequestService techRequestService = services.techRequestService;
    static final StudentOrganizationService organizationService = services.studentOrganizationService;
    static final ResearchService researchService = services.researchService;

    public static MenuBuilder getMainMenu(){

        User activeUser = getActiveUser();

        MenuBuilder menu = new MenuBuilder("University System v0.0001");
        menu.addAction("My Profile", () -> getProfileMenu().start());
        if(activeUser instanceof Student){
            menu.addAction("My Transcript", () -> printStudentTranscript());
            menu.addAction("Student Organization Menu", () -> getStudentOrgMenu().start());
        }
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


    public static void changeLanguage() {
        LanguagePreference choice = UIForms.readLanguagePreference(scanner);
        AppSettings.setLanguage(choice);
        printSuccess(Translator.translate(UIMessage.AUTH_CHANGE_LANG));
    }

    private static MenuBuilder getStudentOrgMenu() {
        User activeUser = getActiveUser();

        boolean isMember = organizationService.isMember(activeUser);
    
        MenuBuilder menu = new MenuBuilder("Student Organizations");
        menu.addAction("View all organizations", () -> printAllStudentOrgs());
        menu.addAction("View organization detail", () -> printAllStudentOrgs());
        if(isMember){
            menu.addAction("View my organization", () -> getMyOrgMenu().start());
        }else{
            menu.addAction("Join an organization", () -> joinStudentOrg());
            menu.addAction("Create an organization", () -> createStudentOrg());
        }
        
        menu.addAction("Exit", () -> menu.stop());
        return menu;
    }

    private static void joinStudentOrg() {
        Student student = (Student) getActiveUser();
        List<StudentOrganization> orgs = organizationService.getAll();
        if(orgs.isEmpty()){
            println("No organizations");
            return;
        }
        StudentOrganization org = UIForms.readIdFromList(scanner, UIMessage.INPUT_ORG_ID, orgs);
        organizationService.addMember(org, student);
        printSuccess("Joined " + org.getName() + ".");
    }

    private static void createStudentOrg() {
        Student student = (Student) getActiveUser();
        String name = UIForms.readNonEmpty(scanner, UIMessage.INPUT_ORG_NAME);
        String description = UIForms.readNonEmpty(scanner, UIMessage.INPUT_ORG_DESC);
        StudentOrganization org = new StudentOrganization(name, description, student);
        organizationService.create(org);
        printSuccess("Organization \"" + name + "\" created.");
    }


    private static void printAllStudentOrgs() {
        List<StudentOrganization> orgs = organizationService.getAll();
        if(orgs.isEmpty()){
            println("No organizations");
            return;
        }
        printHeader("Organizations");
        orgs.forEach(o -> println(o.asLine()));
    }


    private static MenuBuilder getMyOrgMenu() {
        User activeUser = getActiveUser();

        StudentOrganization org = organizationService.getOrganizationByMember(activeUser);
        MenuBuilder menu = new MenuBuilder("");
        menu.addLabel(org.asTable());
        if(org.getPresident().equals(activeUser)){
            menu.addAction("Add member", () -> {
                addMemberToOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction("Remove member", () -> {
                removeMemberFromOrg(org);
                menu.stop();
                getMyOrgMenu().start();
            });
            menu.addAction("Delete organization", () -> {
                deleteStudentOrg(org);
                menu.stop();
            });
        }else{
            menu.addAction("Leave organization", () -> getMyOrgMenu().start());
        }
        menu.addAction("Exit", () -> menu.stop());
        return menu;
    }

    private static void addMemberToOrg(StudentOrganization org) {
        List<Student> candidates = userService.getAllByClass(Student.class).stream()
                .filter(s -> !organizationService.isMember(s))
                .toList();
        if(candidates.isEmpty()){
            printFail("No students available to add.");
            return;
        }
        printHeader("Students");
        candidates.forEach(s -> println(s.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIMessage.INPUT_STUDENT_ID, candidates);
        organizationService.addMember(org, student);
        printSuccess(student.getFullname() + " added to " + org.getName() + ".");
    }

    private static void removeMemberFromOrg(StudentOrganization org) {
        List<Student> removable = org.getMembers().stream()
                .filter(m -> !m.equals(org.getPresident()))
                .toList();
        if(removable.isEmpty()){
            printFail("No members to remove.");
            return;
        }
        printHeader("Members");
        removable.forEach(m -> println(m.asLine()));
        Student student = UIForms.readIdFromList(scanner, UIMessage.INPUT_STUDENT_ID, removable);
        organizationService.removeMember(org, student);
        printSuccess(student.getFullname() + " removed from " + org.getName() + ".");
    }

    private static void deleteStudentOrg(StudentOrganization org) {
        organizationService.delete(org);
        printSuccess("Organization \"" + org.getName() + "\" deleted.");
    }


    private static void printStudentTranscript() {
        Student student = (Student) getActiveUser();
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(student);
        if(enrollments.isEmpty()){
            printFail("You are not enrolled on any course yet");
            return;
        }
        println("\n" + student.asLine());
        println("Overall GPA: " + enrollmentService.getStudentGpa(student));
        println("--------------------------");
        for (Enrollment enr : enrollments) {
            println(enr.asTable());
            println("--------------------------");
        }
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
        User activeUser = getActiveUser();
        MenuBuilder menu = new MenuBuilder("Research Menu");
        menu.addAction("View all researchers", () -> printAllResearchers());
        menu.addAction("View researcher papers", () -> printResearcherPapers());
        menu.addAction("View all papers", () -> getPaperMenu().start());
        if(!researchService.isResearcher(activeUser)){
            menu.addAction("Become researcher", () -> {
                becomeResearcher();
                menu.stop();
                menu.start();
            });
        }else{
            menu.addAction("Manage my papers", () -> getManagePapersMenu().start());
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void printAllResearchers() {
        List<ResearcherProfile> profiles = researchService.getAll();
        if(profiles.isEmpty()){
            println("No researchers");
            return;
        }
        printHeader("Researchers");
        profiles.forEach(p -> println(p.asLine()));
    }

    private static void becomeResearcher() {
        researchService.createResearcherProfile(getActiveUser());
        printSuccess("You are now a researcher.");
    }

    private static void printResearcherPapers() {
        List<ResearcherProfile> profiles = researchService.getAll();
        if(profiles.isEmpty()){
            println("No researchers");
            return;
        }
        printHeader("Researchers");
        profiles.forEach(p -> println(p.asLine()));
        ResearcherProfile profile = UIForms.readIdFromList(scanner, UIMessage.INPUT_RESEARCH_PROFILE_USER_ID, profiles);
        List<ResearchPaper> papers = researchService.getPapersByAuthor(profile.getUser());
        if(papers.isEmpty()){
            printFail(profile.getUser().getFullname() + " has no papers.");
            return;
        }
        printHeader(profile.getUser().getFullname() + "'s Papers");
        papers.forEach(p -> println(p.asLine()));
    }

    private static MenuBuilder getPaperMenu() {
        MenuBuilder menu = new MenuBuilder("Papers");
        menu.addAction("View all (by publish date)", () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_DATE));
        menu.addAction("View all (by citations)", () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_CITATIONS_DESC));
        menu.addAction("View all (by views)", () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_VIEWS_DESC));
        menu.addAction("View paper detail", () -> openPaper());
        menu.addAction("Cite a paper", () -> citePaper());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void printPapersSorted(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> papers = researchService.getAllPapers().stream().sorted(comparator).toList();
        if(papers.isEmpty()){
            println("No papers");
            return;
        }
        printHeader("Papers");
        papers.forEach(p -> println(p.asLine()));
    }

    private static void openPaper() {
        List<ResearchPaper> papers = researchService.getAllPapers();
        if(papers.isEmpty()){
            println("No papers");
            return;
        }
        printHeader("Papers");
        papers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, papers);
        researchService.incrementViews(paper);
        println(paper.asTable());
        if(!paper.getResearchers().isEmpty()){
            printHeader("Participants");
            paper.getResearchers().forEach(r -> println(r.getUser().asLine()));
        }
    }

    private static void citePaper() {
        List<ResearchPaper> papers = researchService.getAllPapers();
        if(papers.isEmpty()){
            println("No papers");
            return;
        }
        printHeader("Papers");
        papers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, papers);
        researchService.citePaper(paper);
        printSuccess("Paper cited.");
    }

    private static MenuBuilder getManagePapersMenu() {
        MenuBuilder menu = new MenuBuilder("Manage Papers");
        menu.addAction("Create paper", () -> createPaper());
        menu.addAction("Delete paper", () -> deletePaper());
        menu.addAction("Add participant", () -> addPaperParticipant());
        menu.addAction("Remove participant", () -> removePaperParticipant());
        menu.addAction("Add reference", () -> addPaperReference());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void createPaper() {
        String title = UIForms.readNonEmpty(scanner, UIMessage.INPUT_PAPER_TITLE);
        ResearchPaper paper = researchService.createPaper(getActiveUser(), title);
        printSuccess("Paper \"" + paper.getTitle() + "\" created.");
    }

    private static void deletePaper() {
        List<ResearchPaper> myPapers = researchService.getPapersByAuthor(getActiveUser());
        if(myPapers.isEmpty()){
            printFail("You have no papers.");
            return;
        }
        printHeader("My Papers");
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, myPapers);
        researchService.deletePaper(paper);
        printSuccess("Paper deleted.");
    }

    private static void addPaperParticipant() {
        List<ResearchPaper> myPapers = researchService.getPapersByAuthor(getActiveUser());
        if(myPapers.isEmpty()){
            printFail("You have no papers.");
            return;
        }
        printHeader("My Papers");
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> candidates = researchService.getAll().stream()
                .filter(rp -> paper.getResearchers().stream().noneMatch(r -> r.getId() == rp.getId()))
                .toList();
        if(candidates.isEmpty()){
            printFail("No researchers available.");
            return;
        }
        printHeader("Researchers");
        candidates.forEach(c -> println(c.asLine()));
        ResearcherProfile participant = UIForms.readIdFromList(scanner, UIMessage.INPUT_RESEARCH_PROFILE_USER_ID, candidates);
        researchService.addParticipant(paper, participant);
        printSuccess("Participant added.");
    }

    private static void removePaperParticipant() {
        ResearcherProfile myProfile = researchService.getProfile(getActiveUser());
        List<ResearchPaper> myPapers = researchService.getPapersByAuthor(getActiveUser());
        if(myPapers.isEmpty()){
            printFail("You have no papers.");
            return;
        }
        printHeader("My Papers");
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> participants = paper.getResearchers().stream()
                .filter(rp -> rp.getId() != myProfile.getId())
                .toList();
        if(participants.isEmpty()){
            printFail("No other participants to remove.");
            return;
        }
        printHeader("Participants");
        participants.forEach(p -> println(p.asLine()));
        ResearcherProfile target = UIForms.readIdFromList(scanner, UIMessage.INPUT_RESEARCH_PROFILE_USER_ID, participants);
        researchService.removeParticipant(paper, target);
        printSuccess("Participant removed.");
    }

    private static void addPaperReference() {
        List<ResearchPaper> myPapers = researchService.getPapersByAuthor(getActiveUser());
        if(myPapers.isEmpty()){
            printFail("You have no papers.");
            return;
        }
        printHeader("My Papers");
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, myPapers);

        List<ResearchPaper> candidates = researchService.getAllPapers().stream()
                .filter(p -> p.getId() != paper.getId())
                .filter(p -> paper.getReferences().stream().noneMatch(r -> r.getId() == p.getId()))
                .toList();
        if(candidates.isEmpty()){
            printFail("No papers available to reference.");
            return;
        }
        printHeader("Available Papers");
        candidates.forEach(p -> println(p.asLine()));
        ResearchPaper reference = UIForms.readIdFromList(scanner, UIMessage.INPUT_PAPER_ID, candidates);
        researchService.addReference(paper, reference);
        printSuccess("Reference added.");
    }

    private static MenuBuilder getNewsMenu() {
        List<News> news = newsService.getAll();
        MenuBuilder menu = new MenuBuilder("News Menu");
        if(news.isEmpty()){
            menu.addLabel("No news");
        }else{
            for(News n : news){
                menu.addAction(n.asLine(), () -> openNews(n));
            }
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void openNews(News news) {
        News updatedNews = newsService.get(news);
        MenuBuilder menu = new MenuBuilder("");
        menu.addLabel(updatedNews.asTable());
        menu.addAction("Leave a comment", () -> {
            leaveComment(updatedNews);
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
        User activeUser = getActiveUser();
        MenuBuilder menu = new MenuBuilder("Course Menu");
        menu.addAction("View all courses", () -> printAllCourses());
        menu.addAction("View all teachers", () -> printAllTeachersForCourseMenu());
        if (activeUser instanceof Student) {
            menu.addAction("Enroll to a course", () -> enrollStudentInCourse((Student) activeUser));
        }

        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void printAllTeachersForCourseMenu() {
        List<Teacher> teachers = userService.getAllByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println("No teachers.");
            return;
        }
        printHeader("Teachers");
        teachers.forEach(t -> println(t.asLine()));
    }

    private static void enrollStudentInCourse(Student student) {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                println("No courses.");
                return;
            }
            println("Choose a course:");
            courses.forEach(c -> println(c.asLine()));
            Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);

            List<Teacher> lectures = course.getLectureTeachers();
            List<Teacher> practices = course.getPracticeTeachers();

            if (lectures.isEmpty() || practices.isEmpty()) {
                printFail("This course must have at least one lecture teacher and one practice teacher before you can enroll.");
                return;
            }

            println("Choose your lecture teacher:");
            lectures.forEach(l -> println(l.asLine()));
            Teacher lectureTeacher = UIForms.readIdFromList(scanner, UIMessage.INPUT_TEACHER_ID, lectures);

            println("Choose your practice teacher:");
            practices.forEach(p -> println(p.asLine()));
            Teacher practiceTeacher = UIForms.readIdFromList(scanner, UIMessage.INPUT_TEACHER_ID, practices);

            enrollmentService.create(new Enrollment(course, student, lectureTeacher, practiceTeacher));
            printSuccess("Enrolled in " + course.getName() + ".");
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
        printHeader("Employees");
        List<Employee> employees = userService.getAllByClass(Employee.class);
        if (employees.isEmpty()) {
            println("No employees.");
            return;
        }
        employees.forEach(e -> println(e.asLine()));
        Employee emp = UIForms.readIdFromList(scanner, UIMessage.INPUT_EMPLOYEE_ID, employees);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        messageService.sendMessage(new Message(activeUser, content), emp);
    }


    private static void openChat(Chat chat) {
        Employee employee = (Employee) getActiveUser();
        MenuBuilder menu = new MenuBuilder("");
        chat.getMessages().forEach(msg -> menu.addLabel(msg.asLine()));
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
        menu.addAction("View my requests", () -> printTechRequests());
        menu.addAction("Send a new request", () -> sendTechRequest());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void sendTechRequest() {
        Employee employee = (Employee) getActiveUser();
        List<TechSupportSpecialist> specialists = userService.getAllByClass(TechSupportSpecialist.class);
        if (specialists.isEmpty()) {
            printFail("No tech support specialists available.");
            return;
        }
        printHeader("Tech Support Specialists");
        specialists.forEach(s -> println(s.asLine()));
        TechSupportSpecialist specialist = UIForms.readIdFromList(scanner, UIMessage.INPUT_RECEIVER_ID, specialists);
        String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);
        techRequestService.create(new TechRequest(employee, specialist, content));
        printSuccess("Tech request sent.");
    }


    private static void printTechRequests() {
        Employee employee = (Employee) getActiveUser();
        List<TechRequest> requests = techRequestService.getAllByEmployee(employee);
        if (requests.isEmpty()) {
            printFail("You have no tech requests yet.");
            return;
        }
        requests.forEach(r -> println(r.asLine()));
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
            if (deans.isEmpty()) {
                println("No deans.");
                return;
            }
            printHeader("Deans");
            deans.forEach(d -> println(d.asLine()));
            Dean dean = UIForms.readIdFromList(scanner, UIMessage.INPUT_RECEIVER_ID, deans);

            List<Student> students = userService.getAllByClass(Student.class);
            if (students.isEmpty()) {
                println("No students.");
                return;
            }
            printHeader("Students");
            students.forEach(s -> println(s.asLine()));
            Student student = UIForms.readIdFromList(scanner, UIMessage.INPUT_STUDENT_ID, students);

            ComplaintUrgencyLevel urgency = UIForms.readComplaintUrgencyLevel(scanner);
            String content = UIForms.readNonEmpty(scanner, UIMessage.INPUT_MESSAGE_CONTENT);

            TeacherComplaint complaint = new TeacherComplaint(urgency, teacher, dean, student, content);
            complaintService.create(complaint);
            printSuccess("Complaint sent.");
    }

    private static void closeComplaint() {
            Dean dean = (Dean) getActiveUser();
            List<TeacherComplaint> pending = complaintService.getDeanComplaints(dean);
            if (pending.isEmpty()) {
                println("No pending complaints.");
                return;
            }
            printHeader("Complaints");
            pending.forEach(tc -> println(tc.asLine()));
            TeacherComplaint complaint = UIForms.readIdFromList(scanner, UIMessage.INPUT_REQUEST_ID, pending);
            complaintService.closeComplaint(complaint, dean);
            printSuccess("Complaint closed.");
    }

    private static void printTeacherComplaints() {
        Teacher activeUser = (Teacher) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getTeacherComplaints(activeUser);
        if(complaints.isEmpty()){
            printFail("You have no complaints yet,");
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }

    private static void printDeanComplaints() {
        Dean activeUser = (Dean) getActiveUser();
        List<TeacherComplaint> complaints = complaintService.getDeanComplaints(activeUser);
        if(complaints.isEmpty()){
            printFail("You have no complaints yet,");
            return;
        }
        complaints.forEach(tc -> println(tc.asLine()));
    }


    private static MenuBuilder getTeacherMenu() {
        MenuBuilder menu = new MenuBuilder("Teacher Menu");
        menu.addAction("View my courses", () -> printTeacherCourses());
        menu.addAction("View my students", () -> printTeacherStudents());
        menu.addAction("Put a mark", () -> putMarkToStudent());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void putMarkToStudent() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Enrollment>> enrollmentMap = teacherService.getTeacherEnrollments(activeUser);
        List<Course> courses = enrollmentMap.keySet().stream().toList();
        if(enrollmentMap.isEmpty()){
            printFail("No enrollments");
            return;
        }

        printHeader("Courses");
        courses.forEach(c -> println(c.asLine()));
        Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);
        List<Enrollment> courseEnrollments = enrollmentMap.get(course);
        printHeader(course.getName() + " Enrollments");
        courseEnrollments.forEach(en -> println(en.asLine()));
        Enrollment enrollment = UIForms.readIdFromList(scanner, UIMessage.INPUT_ENROLLMENT_ID, courseEnrollments);
        AttestationType type = UIForms.readAttestationType(scanner);
        double pointsToAdd = UIForms.readDouble(scanner, UIMessage.INPUT_POINTS_TO_ADD);
        enrollmentService.addPoints(enrollment, activeUser, type, pointsToAdd);
        printSuccess("Mark recorded.");
    }


    private static void printTeacherStudents() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<Course,List<Student>> students = teacherService.getTeacherStudents(activeUser);
        printHeader("Students");
        for(var entry : students.entrySet()){
            printHeader(entry.getKey().getName());
            entry.getValue().forEach(s -> println(s.asLine()));
        }
    }


    private static void printTeacherCourses() {
        Teacher activeUser = (Teacher) getActiveUser();
        Map<TeacherType, List<Course>> courseMap = teacherService.getTeacherCourses(activeUser);
        for(var entry : courseMap.entrySet()){
            printHeader(StringUtils.capitalize(entry.getKey().name()));
            entry.getValue().forEach(c -> println(c.asLine()));
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
        List<User> users = userService.getAllByClass(User.class);
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader("User");
        users.stream().filter(u -> !u.equals(getActiveUser())).forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIMessage.INPUT_USER_ID, users);
        userService.ban(user);
        printSuccess("User " + user.asLine() + " is banned");
    }


    private static void deleteUser() {
        List<User> users = userService.getAllByClass(User.class);
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }
        printHeader("User");
        users.forEach(u -> println(u.asLine()));
        User user = UIForms.readIdFromList(scanner, UIMessage.INPUT_USER_ID, users);
        userService.delete(user);
        printSuccess("User " + user.asLine() + " is deleted");
    }


    private static void createUser() {
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
    }



    private static <U extends User> void printAllUsersByClass(Class<U> className) {
        List<U> users = userService.getAllByClass(className);
        if(users == null || users.isEmpty()){
            println("No users found.");
            return;
        }
        printHeader(className.getSimpleName());
        users.forEach(u -> println(u.asLine()));
    }


    private static void printAllLogsByUserId() {
        int userId = UIForms.readInt(scanner, UIMessage.INPUT_USER_ID);
        List<LogEntry> logs = Logger.getUserLogs(userId);
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(l -> println(l.asLine()));
    }


    private static void printRecentLogs() {
        List<LogEntry> logs = Logger.getRecentLogs();
        if (logs.isEmpty()) {
            println("No recent logs found.");
            return;
        }
        println("Recent logs for last " + AppSettings.RECENT_LOG_HOURS + " hours:");
        logs.forEach(l -> println(l.asLine()));
    }


    private static void printAllLogs() {
        List<LogEntry> logs = Logger.getAllLogs();
        if (logs.isEmpty()) {
            println("No logs found.");
            return;
        }
        logs.forEach(l -> println(l.asLine()));
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
        printHeader("Courses");
        courseService.getAll().forEach(c -> println(c.asLine()));
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
            courseService.delete(course.getId());
            printSuccess("Course deleted.");
    }

    private static void assignTeacherToCourse() {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                println("No courses.");
                return;
            }
            printHeader("Courses");
            courses.forEach(c -> println(c.asLine()));
            Course course = UIForms.readIdFromList(scanner, UIMessage.INPUT_COURSE_ID, courses);
            List<Teacher> teachers = userService.getAllByClass(Teacher.class);
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
            newsService.postNews(news);
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
            newsService.deleteNews(news, manager);
            printSuccess("News deleted.");
    }

    private static void printStudentGPAStats() {
        List<Student> students = userService.getAllByClass(Student.class);
        if (students.isEmpty()) {
            println("No students.");
            return;
        }
        printHeader("Average GPA by student");
        int totalGPA = 0;
        int totalStudents = 0;
        for (Student student : students) {
            List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(student);
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
            List<Enrollment> enrollments = enrollmentService.getCourseEnrollments(course);
            if (enrollments.isEmpty()) {
                println(course.asLine() + " | no enrollments");
                continue;
            }
            double avg = enrollments.stream().mapToDouble(Enrollment::getGpa).average().orElse(0.0);
            println(course.asLine() + " | Avg GPA: " + String.format("%.2f", avg));
        }
    }

    private static void printTeacherGPAStats() {
        List<Teacher> teachers = userService.getAllByClass(Teacher.class);
        if (teachers.isEmpty()) {
            println("No teachers.");
            return;
        }
        printHeader("Average student GPA by teacher");
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
                println(teacher.asLine() + " | no enrollments");
            } else {
                println(teacher.asLine() + " | Avg GPA: " + String.format("%.2f", sum / count));
            }
        }
    }


    private static MenuBuilder getTechSupportSpecMenu() {
        MenuBuilder menu = new MenuBuilder("Technical Specialist Menu");
        menu.addAction("View all requests", () -> printSpecialistRequests());
        menu.addAction("View requests (by status)", () -> printSpecialistRequestsByStatus());
        menu.addAction("Update request", () -> updateTechRequest());
        menu.addAction("Back", () -> menu.stop());
        return menu;
    }

    private static void printSpecialistRequests() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getAllBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail("No requests");
            return;
        }
        requests.forEach(r -> println(r.asLine()));
    }

    private static void printSpecialistRequestsByStatus() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        List<TechRequest> requests = techRequestService.getAllBySpecialist(specialist).stream()
                .filter(r -> r.getStatus() == status)
                .toList();
        if (requests.isEmpty()) {
            printFail("No requests with status " + status + ".");
            return;
        }
        printHeader(status + " Requests");
        requests.forEach(r -> println(r.asLine()));
    }

    private static void updateTechRequest() {
        TechSupportSpecialist specialist = (TechSupportSpecialist) getActiveUser();
        List<TechRequest> requests = techRequestService.getAllBySpecialist(specialist);
        if (requests.isEmpty()) {
            printFail("You have no tech requests.");
            return;
        }
        printHeader("My Requests");
        requests.forEach(r -> println(r.asLine()));
        TechRequest request = UIForms.readIdFromList(scanner, UIMessage.INPUT_REQUEST_ID, requests);
        TechRequestStatus status = UIForms.readTechRequestStatus(scanner);
        request.setStatus(status);
        techRequestService.update(request);
        printSuccess("Request updated.");
    }

    public static MenuBuilder getProfileMenu() {
        MenuBuilder menu = new MenuBuilder("My profile");
        menu.addAction("View profile", () -> println("\n" + getActiveUser().asTable()));
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

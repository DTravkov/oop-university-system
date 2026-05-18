package application.apps;

import java.util.Comparator;
import java.util.List;

import model.domain.Comment;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.News;
import model.domain.Notification;
import model.domain.ResearchPaper;
import model.domain.ResearcherProfile;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import services.CourseService;
import services.EnrollmentService;
import services.NewsService;
import services.NotificationService;
import services.ResearchService;
import services.UserService;
import utils.Comparators;
import utils.UIForms;
import utils.UIText;

/**
 * Application that provides common menus.
 * Common menas they are used by every registered {@link User}
 */
public class CommonMenus extends BaseApp {

    static final UserService userService = services.userService;
    static final CourseService courseService = services.courseService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final NewsService newsService = services.newsService;
    static final ResearchService researchService = services.researchService;
    static final NotificationService notificationService = services.notificationService;

    static MenuBuilder getResearcherMenu() {
        User activeUser = getActiveUser();
        MenuBuilder menu = new MenuBuilder(UIText.RESEARCH_MENU_TITLE);
        menu.addAction(UIText.RESEARCH_VIEW_RESEARCHERS, () -> getAllReseracherViewMenu().start());
        menu.addAction(UIText.RESEARCH_PAPER_MENU, () -> getPaperMenu().start());
        if(!researchService.isResearcher(activeUser)){
            menu.addAction(UIText.RESEARCH_BECOME, () -> {
                becomeResearcher();
                menu.stop();
                getResearcherMenu().start();
            });
        }else{
            menu.addAction(UIText.RESEARCH_MANAGE_PAPERS, () -> getManagePapersMenu().start());
        }
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }


    private static MenuBuilder getPaperMenu() {
        MenuBuilder menu = new MenuBuilder(UIText.RESEARCH_PAPERS_MENU_TITLE);
        menu.addAction(UIText.RESEARCH_PAPERS_BY_DATE, () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_DATE));
        menu.addAction(UIText.RESEARCH_PAPERS_BY_CITATIONS, () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_CITATIONS_DESC));
        menu.addAction(UIText.RESEARCH_PAPERS_BY_VIEWS, () -> printPapersSorted(Comparators.RESEARCH_PAPER_BY_VIEWS_DESC));
        menu.addAction(UIText.RESEARCH_VIEW_PAPER, () -> openPaper());
        menu.addAction(UIText.RESEARCH_CITE_PAPER, () -> citePaper());
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }

    private static MenuBuilder getAllReseracherViewMenu(){
        MenuBuilder menu = new MenuBuilder(UIText.RESEARCH_MENU_RESEARCHERS);
        for(ResearcherProfile profile : researchService.getAllProfiles()){
            menu.addAction("[" +profile.asLine() +"]", () -> openResearcher(profile));
        }
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }

    public static MenuBuilder getNotificationMenu(){
        User user = getActiveUser();

        List<Notification> readNotifications = user.getReadNotifications();
        List<Notification> unreadNotifications = user.getUnreadNotifications();

        MenuBuilder menu = new MenuBuilder(UIText.NOTIFICATION_MENU_TITLE, unreadNotifications.size());
        readNotifications.forEach(n -> menu.addLabel(n.asLine()));
        unreadNotifications.forEach(n -> menu.addLabel(UIText.MSG_NOTIFICATION_UNREAD_PREFIX.localized() + n.asLine()));

        if(readNotifications.size() + unreadNotifications.size() == 0){
            menu.addLabel(UIText.MSG_NO_NOTIFICATIONS.localized());
        }

        menu.addAction(UIText.NOTIFICATION_MARK_ALL_READ, () -> {
            notificationService.markNotificationsRead(user);
            userService.update(user);
            menu.stop();
            getNotificationMenu().start();
        });

        menu.addExit();
        return menu;
    }

    private static void becomeResearcher() {
        researchService.createProfile(getActiveUser());
        printSuccess(UIText.MSG_BECAME_RESEARCHER);
    }

    private static void openResearcher(ResearcherProfile profile){
        println("\n" + profile.asTable());
    }

    private static void printPapersSorted(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> papers = researchService.getAllPapers().stream().sorted(comparator).toList();
        if(papers.isEmpty()){
            println(UIText.MSG_NO_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_PAPERS);
        papers.forEach(p -> println(p.asLine()));
    }

    private static void openPaper() {
        List<ResearchPaper> papers = researchService.getAllPapers();
        if(papers.isEmpty()){
            println(UIText.MSG_NO_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_PAPERS);
        papers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, papers);
        researchService.incrementViews(paper);
        println(paper.asTable());
        if(!paper.getResearchers().isEmpty()){
            printHeader(UIText.RESEARCH_HEADER_PARTICIPANTS);
            paper.getResearchers().forEach(r -> println(r.getUser().asLine()));
        }
    }

    private static void citePaper() {
        List<ResearchPaper> papers = researchService.getAllPapers();
        if(papers.isEmpty()){
            println(UIText.MSG_NO_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_PAPERS);
        papers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, papers);
        researchService.citePaper(paper);
        printSuccess(UIText.MSG_PAPER_CITED);
    }

    private static MenuBuilder getManagePapersMenu() {
        MenuBuilder menu = new MenuBuilder(UIText.RESEARCH_MANAGE_TITLE);
        menu.addAction(UIText.RESEARCH_VIEW_MY_PAPERS, () -> {
            researchService.getAllPapers(getActiveUser()).forEach(p -> println(p.asLine()));
        });
        menu.addAction(UIText.RESEARCH_CREATE_PAPER, () -> createPaper());
        menu.addAction(UIText.RESEARCH_DELETE_PAPER, () -> deletePaper());
        menu.addAction(UIText.RESEARCH_ADD_PARTICIPANT, () -> addPaperParticipant());
        menu.addAction(UIText.RESEARCH_REMOVE_PARTICIPANT, () -> removePaperParticipant());
        menu.addAction(UIText.RESEARCH_ADD_REFERENCE, () -> addPaperReference());
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }

    private static void createPaper() {
        ResearcherProfile profile = researchService.getProfile(getActiveUser());
        String title = UIForms.readNonEmpty(scanner, UIText.INPUT_PAPER_TITLE);
        ResearchPaper paper = researchService.createPaper(new ResearchPaper(title));
        researchService.addParticipant(paper, profile);
        printSuccess(UIText.MSG_PAPER_CREATED, paper.getTitle());
    }

    private static void deletePaper() {
        List<ResearchPaper> myPapers = researchService.getAllPapers(getActiveUser());
        if(myPapers.isEmpty()){
            printFail(UIText.MSG_NO_MY_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_MY_PAPERS);
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);
        researchService.deletePaper(paper);
        printSuccess(UIText.MSG_PAPER_DELETED);
    }

    private static void addPaperParticipant() {
        List<ResearchPaper> myPapers = researchService.getAllPapers(getActiveUser());
        if(myPapers.isEmpty()){
            printFail(UIText.MSG_NO_MY_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_MY_PAPERS);
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> candidates = researchService.getAllProfiles().stream()
                .filter(rp -> paper.getResearchers().stream().noneMatch(r -> r.getId() == rp.getId()))
                .toList();
        if(candidates.isEmpty()){
            printFail(UIText.MSG_NO_RESEARCHERS_AVAILABLE);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_RESEARCHERS);
        candidates.forEach(c -> println(c.asLine()));
        ResearcherProfile participant = UIForms.readIdFromList(scanner, UIText.INPUT_RESEARCH_PROFILE_USER_ID, candidates);
        researchService.addParticipant(paper, participant);
        printSuccess(UIText.MSG_PARTICIPANT_ADDED);
    }

    private static void removePaperParticipant() {
        ResearcherProfile myProfile = researchService.getProfile(getActiveUser());
        List<ResearchPaper> myPapers = researchService.getAllPapers(getActiveUser());
        if(myPapers.isEmpty()){
            printFail(UIText.MSG_NO_MY_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_MY_PAPERS);
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> participants = paper.getResearchers().stream()
                .filter(p -> !p.equals(myProfile))
                .toList();
        if(participants.isEmpty()){
            printFail(UIText.MSG_NO_PARTICIPANTS_TO_REMOVE);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_PARTICIPANTS);
        participants.forEach(p -> println(p.asLine()));
        ResearcherProfile target = UIForms.readIdFromList(scanner, UIText.INPUT_RESEARCH_PROFILE_USER_ID, participants);
        researchService.removeParticipant(paper, target);
        printSuccess(UIText.MSG_PARTICIPANT_REMOVED);
    }

    private static void addPaperReference() {
        ResearcherProfile profile = researchService.getProfile(getActiveUser());
        List<ResearchPaper> myPapers = researchService.getAllPapers(p -> p.getResearchers().contains(profile));
        if(myPapers.isEmpty()){
            printFail(UIText.MSG_NO_MY_PAPERS);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_MY_PAPERS);
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

        List<ResearchPaper> candidates = researchService.getAllPapers().stream()
                .filter(p -> p.getId() != paper.getId())
                .filter(p -> paper.getReferences().stream().noneMatch(r -> r.getId() == p.getId()))
                .toList();
        if(candidates.isEmpty()){
            printFail(UIText.MSG_NO_PAPERS_TO_REFERENCE);
            return;
        }
        printHeader(UIText.RESEARCH_HEADER_AVAILABLE_PAPERS);
        candidates.forEach(p -> println(p.asLine()));
        ResearchPaper reference = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, candidates);
        researchService.addReference(paper, reference);
        printSuccess(UIText.MSG_REFERENCE_ADDED);
    }


    static MenuBuilder getNewsMenu() {
        List<News> news = newsService.getAll();
        MenuBuilder menu = new MenuBuilder(UIText.NEWS_MENU_TITLE);
        if(news.isEmpty()){
            menu.addLabel(UIText.NEWS_NO_NEWS.localized());
        }else{
            for(News n : news){
                menu.addAction("["+n.getTitle()+"]", () -> openNews(n));
            }
        }
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }

    private static void openNews(News news) {
        News updatedNews = newsService.get(news);
        MenuBuilder menu = new MenuBuilder("");
        menu.addLabel(updatedNews.asTable());
        menu.addAction(UIText.NEWS_LEAVE_COMMENT, () -> {
            leaveComment(updatedNews);
            menu.stop();
        });
        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        menu.start();
    }

    private static void leaveComment(News news) {
        User activeUser = getActiveUser();
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_COMMENT);
        newsService.assignComment(news, new Comment(activeUser, content));
        openNews(news);
    }


    static MenuBuilder getCourseMenu() {
        User activeUser = getActiveUser();
        List<Course> courses = courseService.getAll();

        MenuBuilder menu = new MenuBuilder(UIText.COURSE_MENU_TITLE);
        for(var course : courses){
            menu.addAction("[" +course.getName() + "]", () -> println(course.asTable()));
        }
        if (activeUser instanceof Student) {
            menu.addAction(UIText.COURSE_ENROLL, () -> enrollStudentInCourse((Student) activeUser));
        }

        menu.addAction(UIText.MENU_BACK, () -> menu.stop());
        return menu;
    }

    static void printAllCourses() {
        printHeader(UIText.COURSE_HEADER_COURSES);
        courseService.getAll().forEach(c -> println(c.asTable() + "\n"));
    }

    private static void enrollStudentInCourse(Student student) {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                println(UIText.MSG_NO_COURSES);
                return;
            }
            println(UIText.MSG_CHOOSE_COURSE);
            courses.forEach(c -> println(c.asLine()));
            Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);

            List<Teacher> lectures = course.getLectureTeachers();
            List<Teacher> practices = course.getPracticeTeachers();

            if(lectures.size() == 0 || practices.size() == 0){
                println(UIText.NO_COURSE_TEACHERS);
                return;
            }

            println(UIText.MSG_CHOOSE_LECTURE_TEACHER);
            lectures.forEach(l -> println(l.asLine()));
            Teacher lectureTeacher = UIForms.readIdFromList(scanner, UIText.INPUT_TEACHER_ID, lectures);

            println(UIText.MSG_CHOOSE_PRACTICE_TEACHER);
            practices.forEach(p -> println(p.asLine()));
            Teacher practiceTeacher = UIForms.readIdFromList(scanner, UIText.INPUT_TEACHER_ID, practices);

            enrollmentService.create(new Enrollment(course, student, lectureTeacher, practiceTeacher));
            printSuccess(UIText.MSG_ENROLLED_IN, course.getName());
    }


}

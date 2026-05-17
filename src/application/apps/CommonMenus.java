package application.apps;

import java.util.Comparator;
import java.util.List;

import model.domain.Comment;
import model.domain.Course;
import model.domain.Enrollment;
import model.domain.News;
import model.domain.ResearchPaper;
import model.domain.ResearcherProfile;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.User;
import services.CourseService;
import services.EnrollmentService;
import services.NewsService;
import services.ResearchService;
import services.UserService;
import utils.Comparators;
import utils.UIForms;
import utils.UIText;

public class CommonMenus extends BaseApp {

    static final UserService userService = services.userService;
    static final CourseService courseService = services.courseService;
    static final EnrollmentService enrollmentService = services.enrollmentService;
    static final NewsService newsService = services.newsService;
    static final ResearchService researchService = services.researchService;


    static MenuBuilder getResearcherMenu() {
        User activeUser = getActiveUser();
        MenuBuilder menu = new MenuBuilder("Research Menu");
        menu.addAction("View all researchers", () -> printAllResearchers());
        menu.addAction("View researcher papers", () -> printResearcherPapers());
        menu.addAction("View all papers", () -> getPaperMenu().start());
        if(!researchService.isResearcher(activeUser)){
            menu.addAction("Become researcher", () -> {
                becomeResearcher();
                menu.stop();
                getResearcherMenu().start();
            });
        }else{
            menu.addAction("Manage my papers", () -> getManagePapersMenu().start());
        }
        menu.addAction("Back", () -> menu.stop());
        return menu;
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
        researchService.createProfile(getActiveUser());
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
        ResearcherProfile profile = UIForms.readIdFromList(scanner, UIText.INPUT_RESEARCH_PROFILE_USER_ID, profiles);
        List<ResearchPaper> papers = researchService.getPapersByAuthor(profile.getUser());
        if(papers.isEmpty()){
            printFail(profile.getUser().getFullname() + " has no papers.");
            return;
        }
        printHeader(profile.getUser().getFullname() + "'s Papers");
        papers.forEach(p -> println(p.asLine()));
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
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, papers);
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
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, papers);
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
        String title = UIForms.readNonEmpty(scanner, UIText.INPUT_PAPER_TITLE);
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
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);
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
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> candidates = researchService.getAll().stream()
                .filter(rp -> paper.getResearchers().stream().noneMatch(r -> r.getId() == rp.getId()))
                .toList();
        if(candidates.isEmpty()){
            printFail("No researchers available.");
            return;
        }
        printHeader("Researchers");
        candidates.forEach(c -> println(c.asLine()));
        ResearcherProfile participant = UIForms.readIdFromList(scanner, UIText.INPUT_RESEARCH_PROFILE_USER_ID, candidates);
        researchService.addParticipant(paper, participant);
        printSuccess("Participant added.");
    }

    private static void removePaperParticipant() {
        ResearcherProfile myProfile = researchService.getProfileByUser(getActiveUser());
        List<ResearchPaper> myPapers = researchService.getPapersByAuthor(getActiveUser());
        if(myPapers.isEmpty()){
            printFail("You have no papers.");
            return;
        }
        printHeader("My Papers");
        myPapers.forEach(p -> println(p.asLine()));
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

        List<ResearcherProfile> participants = paper.getResearchers().stream()
                .filter(rp -> rp.getId() != myProfile.getId())
                .toList();
        if(participants.isEmpty()){
            printFail("No other participants to remove.");
            return;
        }
        printHeader("Participants");
        participants.forEach(p -> println(p.asLine()));
        ResearcherProfile target = UIForms.readIdFromList(scanner, UIText.INPUT_RESEARCH_PROFILE_USER_ID, participants);
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
        ResearchPaper paper = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, myPapers);

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
        ResearchPaper reference = UIForms.readIdFromList(scanner, UIText.INPUT_PAPER_ID, candidates);
        researchService.addReference(paper, reference);
        printSuccess("Reference added.");
    }


    static MenuBuilder getNewsMenu() {
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
        String content = UIForms.readNonEmpty(scanner, UIText.INPUT_COMMENT);
        newsService.assignComment(news, new Comment(activeUser, content));
        openNews(news);
    }


    static MenuBuilder getCourseMenu() {
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

    static void printAllCourses() {
        printHeader("Courses");
        courseService.getAll().forEach(c -> println(c.asTable() + "\n"));
    }

    private static void printAllTeachersForCourseMenu() {
        List<Teacher> teachers = userService.getUsersByClass(Teacher.class);
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
            Course course = UIForms.readIdFromList(scanner, UIText.INPUT_COURSE_ID, courses);

            List<Teacher> lectures = course.getLectureTeachers();
            List<Teacher> practices = course.getPracticeTeachers();

            if (lectures.isEmpty() || practices.isEmpty()) {
                printFail("This course must have at least one lecture teacher and one practice teacher before you can enroll.");
                return;
            }

            println("Choose your lecture teacher:");
            lectures.forEach(l -> println(l.asLine()));
            Teacher lectureTeacher = UIForms.readIdFromList(scanner, UIText.INPUT_TEACHER_ID, lectures);

            println("Choose your practice teacher:");
            practices.forEach(p -> println(p.asLine()));
            Teacher practiceTeacher = UIForms.readIdFromList(scanner, UIText.INPUT_TEACHER_ID, practices);

            enrollmentService.create(new Enrollment(course, student, lectureTeacher, practiceTeacher));
            printSuccess("Enrolled in " + course.getName() + ".");
    }


}

package model.factories;
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

public final class ServiceRegistry {

    private static final ServiceRegistry INSTANCE = new ServiceRegistry();

    public final UserService userService;
    public final EnrollmentService enrollmentService;
    public final CourseService courseService;
    public final TeacherService teacherService;
    public final ComplaintService complaintService;
    public final MessageService messageService;
    public final NewsService newsService;
    public final TechRequestService techRequestService;
    public final StudentOrganizationService studentOrganizationService;
    public final ResearchService researchService;

    private ServiceRegistry() {
        userService = new UserService();
        enrollmentService = new EnrollmentService();
        courseService = new CourseService(enrollmentService);
        teacherService = new TeacherService(courseService, enrollmentService);
        complaintService = new ComplaintService(userService);
        messageService = new MessageService();
        newsService = new NewsService();
        techRequestService = new TechRequestService(userService);
        studentOrganizationService = new StudentOrganizationService();
        researchService = new ResearchService();
    }

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
}

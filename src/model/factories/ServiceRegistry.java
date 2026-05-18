package model.factories;

import services.ComplaintService;
import services.CourseService;
import services.EnrollmentService;
import services.MessageService;
import services.NewsService;
import services.NotificationService;
import services.ResearchService;
import services.StudentOrganizationService;
import services.TeacherService;
import services.TechRequestService;
import services.UserService;


/**
 * singleton registry for services, that initializes services
 * its used in {@link BaseApp} to proivde services to any application class.
 */
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
    public final NotificationService notificationService;

    private ServiceRegistry() {
        //basic services, exist on their own
        userService = new UserService();
        enrollmentService = new EnrollmentService();
        messageService = new MessageService();
        newsService = new NewsService();
        studentOrganizationService = new StudentOrganizationService();
        researchService = new ResearchService();
        complaintService = new ComplaintService();
        courseService = new CourseService();

        // services that depend on basic services
        techRequestService = new TechRequestService(userService);

        // helper services that are not related to any domain class directly
        teacherService = new TeacherService(courseService, enrollmentService, userService);
        notificationService = new NotificationService(userService);
    }

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
}

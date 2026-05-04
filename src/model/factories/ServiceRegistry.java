package model.factories;

import services.CommentService;
import services.ComplaintService;
import services.CourseService;
import services.EnrollmentService;
import services.MessageService;
import services.NewsService;
import services.ResearchService;
import services.StudentOrganizationService;
import services.TechRequestService;
import services.UserService;

public final class ServiceRegistry {

    private static final ServiceRegistry INSTANCE = new ServiceRegistry();

    public final UserService userService;
    public final ResearchService researchService;
    public final CourseService courseService;
    public final MessageService messageService;
    public final CommentService commentService;
    public final ComplaintService complaintService;
    public final StudentOrganizationService studentOrganizationService;
    public final TechRequestService techRequestService;
    public final NewsService newsService;
    public final EnrollmentService enrollmentService;

    private ServiceRegistry() {
        userService = new UserService();
        researchService = new ResearchService(userService);
        courseService = new CourseService(userService);
        messageService = new MessageService(userService);
        commentService = new CommentService(userService);
        complaintService = new ComplaintService(userService);
        studentOrganizationService = new StudentOrganizationService(userService);
        techRequestService = new TechRequestService(userService);
        newsService = new NewsService(userService, commentService);
        enrollmentService = new EnrollmentService(userService, courseService);
    }

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
}

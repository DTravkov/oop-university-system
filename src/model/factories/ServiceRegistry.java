package model.factories;
import services.ComplaintService;
import services.CourseService;
import services.MessageService;
import services.NewsService;
import services.TeacherService;
import services.UserService;

public final class ServiceRegistry {

    private static final ServiceRegistry INSTANCE = new ServiceRegistry();

    public final UserService userService;
    public final CourseService courseService;
    public final TeacherService teacherService;
    public final ComplaintService complaintService;
    public final MessageService messageService;
    public final NewsService newsService;

    private ServiceRegistry() {
        userService = new UserService();
        courseService = new CourseService();
        teacherService = new TeacherService(courseService);
        complaintService = new ComplaintService(userService);
        messageService = new MessageService();
        newsService = new NewsService();
    }

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
}

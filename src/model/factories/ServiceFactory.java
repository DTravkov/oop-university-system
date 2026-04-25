package model.factories;


import services.*;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();
    private final Map<Class<? extends IService>, IService> services = new HashMap<>();

    private ServiceFactory() {
        initServices();
    }

    private void initServices() {
        UserService userService = new UserService();
        CourseService courseService = new CourseService(userService);
        EnrollmentService enrollmentService = new EnrollmentService(userService, courseService);
        ComplaintService complaintService = new ComplaintService(userService);
        MessageService messageService = new MessageService(userService);
        StudentOrganizationService studentOrganizationService = new StudentOrganizationService(userService);

        services.put(UserService.class, userService);
        services.put(CourseService.class, courseService);
        services.put(EnrollmentService.class, enrollmentService);
        services.put(ComplaintService.class, complaintService);
        services.put(MessageService.class, messageService);
        services.put(StudentOrganizationService.class, studentOrganizationService);
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    public <T extends IService> T getService(Class<T> type) {
        return type.cast(services.get(type));
    }
}
package model.factories;


import services.*;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    private final Map<Class<? extends IService>, IService> services = new HashMap<>();

    private static ServiceFactory instance;

    private ServiceFactory() {
        instance = this;
        services.put(UserService.class, new UserService());
        services.put(CourseService.class, new CourseService());
        services.put(EnrollmentService.class, new EnrollmentService());
        services.put(ComplaintService.class, new ComplaintService());
        services.put(MessageService.class, new MessageService());
        services.put(StudentOrganizationService.class, new StudentOrganizationService());
    }

    public <T extends IService> T getService(Class<T> serviceClass){
        return serviceClass.cast(services.get(serviceClass));
    }

    public static ServiceFactory getInstance(){
        if(instance == null) new ServiceFactory();
        return instance;
    }
}

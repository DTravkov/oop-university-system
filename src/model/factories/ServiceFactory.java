package model.factories;


import services.*;

import java.util.HashMap;

public class ServiceFactory {

    private HashMap<Class<? extends IService>, IService> services = new HashMap<>();

    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private ServiceFactory() {
        services.put(UserService.class, new UserService());
        services.put(CourseService.class, new CourseService());
        services.put(EnrollmentService.class, new EnrollmentService());
        services.put(ComplaintService.class, new ComplaintService());
        services.put(MessageService.class, new MessageService());
    }

    public <T extends IService> T getService(Class<T> serviceClass){
        return serviceClass.cast(services.get(serviceClass));
    }

    public static ServiceFactory getInstance(){
        return INSTANCE;
    }
}

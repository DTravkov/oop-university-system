package services;



import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.EventSystem;

/**
 * BaseService is an abstract class that gives minimal service capabilities
 * With it, we can creare any service from scratch, using pure repository.
 * (  also inspired by DRF View levels :)  ) 
 */
public abstract class BaseService<T extends SerializableModel> {

    protected final Repository<T> repository;
    protected final EventSystem eventSystem;
    protected String baseName;

    protected BaseService(Class<T> className) {
        this.repository = new Repository<T>(className);
        this.eventSystem = EventSystem.getInstance();
        this.baseName = this.getClass().getSimpleName().replace("Service", "");
        subscribeToEvents();
    }


    
    // EVENT HANDLING

    protected void subscribeToEvents() {
        // override and call in constructor if service needs to listen for events
    }

    



}

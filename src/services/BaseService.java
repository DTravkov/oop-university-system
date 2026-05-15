package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

import java.util.List;
import java.util.function.Predicate;

import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.EventSystem;
import utils.Logger;

/**
 * BaseService is an abstract class that gives CRUD methods, provides serialization and data access through repositories.
 * Each service has a reference to the event system; subscriptions are meant for side effects (cleanup, cascades), not core flows.
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

    // CREATE / UPDATE / DELETE

    public T create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(baseName + " with id " + entity.getId());
        }
        Logger.log("Create " + baseName + " (" + entity.getId() + ")");
        return repository.save(entity);
    }

    public void update(T entity){
        if(!repository.exists(entity)){
            throw new DoesNotExist(baseName + " object with id : " + entity.getId());
        }
        if(entity.getId() == 0){
            throw new OperationNotAllowed( baseName + " non-existing object can not be updated");
        }
        Logger.log("Update " + baseName + " (" + entity.getId() + ")");
        repository.save(entity);
    }

    public void delete(T entity){
        if(!repository.exists(entity)){
            throw new DoesNotExist( baseName + " object with id " + entity.getId());
        }
        Logger.log("Delete " + baseName + " (" + entity.getId() + ")");
        repository.delete(entity);
    }


    // QUERIES

    public T get(int id){
        return repository.getAll().stream()
                                .filter(entity -> entity.getId() == id)
                                .findFirst()
                                .orElseThrow(()-> new DoesNotExist(baseName + " with id=" + id));
    }

    public T get(T entity){
        return repository.getAll().stream()
                                .filter(e -> e.equals(entity))
                                .findFirst()
                                .orElseThrow(()-> new DoesNotExist(baseName + " with id=" + entity.getId()));
    }

    public T find(Predicate<T> query){
        return repository.find(query);
    }

    public List<T> getAll() {
        return repository.getAll();
    }
    public List<T> getAll(Predicate<T> query) {
        return repository.getAll().stream().filter(query).toList();
    }

    public boolean exists(T entity) {
        return repository.exists(entity);
    }


    // EVENT HANDLING

    protected void subscribeToEvents() {
        // override and call in constructor if service needs to listen for events
    }

    



}

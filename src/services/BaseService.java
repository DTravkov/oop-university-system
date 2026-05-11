package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

import java.util.List;
import java.util.function.Predicate;

import model.domain.Enrollment;
import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.EventSystem;
import services.events.IObserver;
import utils.Logger;

public abstract class BaseService<T extends SerializableModel> implements IService, IObserver {

    protected final Repository<T> repository;
    protected final EventSystem eventSystem;
    protected String baseName;

    protected BaseService(Class<T> className) {
        this.repository = new Repository<T>(className);
        this.eventSystem = EventSystem.getInstance();
        this.baseName = this.getClass().getSimpleName().replace("Service", "");

    }

    public T create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(baseName + " with id " + entity.getId());
        }
        Logger.log("Create " + baseName + " (" + entity + ")");
        return repository.save(entity);
    }
    

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

    public void update(T entity){
        if(!repository.exists(entity.getId())){
            throw new DoesNotExist(baseName + " object with id : " + entity.getId());
        }
        if(entity.getId() == 0){
            throw new OperationNotAllowed( baseName + " non-existing object can not be updated");
        }
        Logger.log("Update " + baseName + " (" + entity + ")");
        repository.save(entity);
    }

    public void delete(T entity){
        if(!repository.exists(entity)){
            throw new DoesNotExist( baseName + " object with id " + entity.getId());
        }
        Logger.log("Delete " + baseName + " (" + entity + ")");
        repository.delete(entity);
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist( baseName + " object with id " + id);
        }
        Logger.log("Delete " + baseName + " id=" + id);
        repository.delete(id);
    }



    public List<T> getAll() {
        return repository.getAll();
    }

    @Override
    public void subscribeToEvents() {
        // override and call in constructor if service needs to listen for events
    }



}

package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

import java.util.List;

import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.EventSystem;
import services.events.IObserver;

public abstract class BaseService<T extends SerializableModel, R extends Repository<T>> implements IService, IObserver {

    protected final R repository;
    protected final EventSystem eventSystem;
    private String baseName;

    protected BaseService(R repository) {
        this.repository = repository;
        this.eventSystem = EventSystem.getInstance();
        this.baseName = this.getClass().getSimpleName().replace("Service", "");
    }

    public T create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(baseName + " with id " + entity.getId());
        }
        return repository.save(entity);
    }

    public T get(int id){
        T entity = repository.find(id);

        if(entity == null) 
            throw new DoesNotExist(baseName + " object with id : " + id);
        
        return entity;
    }

    public void update(T entity){
        // used to save changes of ALREADY existing object.
        // this will not throw any excpetions if entity is fetched and edited object from repository.data map
        if(!repository.exists(entity.getId())){
            throw new DoesNotExist(baseName + " object with id : " + entity.getId());
        }
        if(entity.getId() == 0){
            throw new OperationNotAllowed( baseName + " non-existing object can not be updated");
        }
        repository.save(entity);
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist( baseName + " object with id " + id);
        }
        repository.delete(id);
    }



    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public void subscribeToEvents() {
        // override if needed to listen for events
    }



}

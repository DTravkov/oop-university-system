package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import java.util.List;

import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.EventSystem;
import services.events.IObserver;

public abstract class BaseService<T extends SerializableModel, R extends Repository<T>> implements IService, IObserver {

    protected final R repository;
    protected final EventSystem eventSystem;

    protected BaseService(R repository) {
        this.repository = repository;
        this.eventSystem = EventSystem.getInstance();
    }

    public T create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(entity.getClass().getSimpleName().replace("Service", "") + " with id " + entity.getId());
        }
        return repository.save(entity);
    }

    public T get(int id){
        T entity = repository.find(id);

        if(entity == null) 
            throw new DoesNotExist(this.getClass().getSimpleName().replace("Service", "") + " object with id : " + id);
        
        return entity;
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist( this.getClass().getSimpleName().replace("Service", "") + " object with id " + id);
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

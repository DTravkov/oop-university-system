package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import java.util.List;

import model.domain.SerializableModel;
import model.repository.IRepository;
import services.events.EventSystem;
import services.events.IObserver;

public abstract class BaseService<T extends SerializableModel, R extends IRepository<T>> implements IService, IObserver {

    protected final R repository;
    protected final EventSystem eventSystem;

    protected BaseService(R repository) {
        this.repository = repository;
        this.eventSystem = EventSystem.getInstance();
    }

    public void create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(entity.getClass().getSimpleName() + " with id " + entity.getId());
        }
        repository.save(entity);
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist( this.getClass().getSimpleName() + " object with id " + id);
        }
        repository.delete(id);
    }

    public T get(int id){
        return repository.find(id)
                .orElseThrow(() -> new DoesNotExist( this.getClass().getSimpleName() + " object with id " + id));
    }

    public List<T> getAll() {
        return repository.findAll();
    }



}

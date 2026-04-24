package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import java.util.Collection;
import model.domain.SerializableModel;
import model.repository.Repository;
import services.events.Event;
import services.events.EventSystem;
import services.events.IObserver;

public abstract class BaseService<T extends SerializableModel, R extends Repository<T>> implements IService, IObserver {

    protected final R repository;
    protected final EventSystem eventSystem;

    protected BaseService(R repository) {
        this.repository = repository;
        this.eventSystem = EventSystem.getInstance();
        EventSystem.getInstance().subscribe(this);
    }

    @Override
    public void update(Event event){
        // override me in other services
    }

    public void create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(entity.getClass().getSimpleName() + " with id " + entity.getId());
        }
        repository.save(entity);
    }

    public T get(int id){
        return repository.find(id)
                .orElseThrow(() -> new DoesNotExist(" object with id " + id));
    }

    public Collection<T> getAll() {
        return repository.findAll();
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist(" object with id " + id);
        }
        repository.delete(id);
    }


}

package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import java.util.Collection;
import model.domain.SerializableModel;
import model.repository.Repository;

public abstract class BaseService<T extends SerializableModel, R extends Repository<T>> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    public void create(T entity){
        if(repository.exists(entity.getId())){
            throw new AlreadyExists(entity.getClass().getSimpleName() + " with id " + entity.getId());
        }
        repository.save(entity);
    }

    public T get(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist("user with id " + id);
        }
        return repository.find(id);
    }

    public Collection<T> getAll() {
        return repository.getAll();
    }

    public void delete(int id){
        if(!repository.exists(id)){
            throw new DoesNotExist("Object with id " + id);
        }
        repository.delete(id);
    }


}

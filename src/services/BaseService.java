package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import java.util.Collection;
import model.domain.SerializableModel;
import model.repository.IRepository;
import model.repository.Repository;

public abstract class BaseService<T extends SerializableModel, R extends Repository<T>> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    public T get(int id) {
        return repository.get(id)
                .orElseThrow(() -> new DoesNotExist("Object with id " + id));
    }

    public Collection<T> getAll() {
        return repository.getAll();
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    public void delete(T entity) {
        repository.delete(entity);
    }

    public boolean exists(T entity){
        return repository.exists(entity);
    }

    public boolean exists(int id){
        return repository.exists(id);
    }

    public void throwIfExist(T entity){
        if (repository.exists(entity)){
            throw new AlreadyExists();
        }
    }
    public void throwIfExist(int id){
        if (repository.exists(id)){
            throw new AlreadyExists();
        }
    }
    public void throwIfNotExist(T entity){
        if (!repository.exists(entity)){
            throw new DoesNotExist();
        }
    }
    public void raiseIfNotExist(int id){
        if (!repository.exists(id)){
            throw new DoesNotExist();
        }
    }


}

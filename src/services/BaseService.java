package services;

import java.util.Collection;

import exceptions.DoesNotExist;
import model.domain.SerializableModel;
import model.repository.BaseRepository;

public abstract class BaseService<T extends SerializableModel, R extends BaseRepository<T>> {
    
    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }
    
    public T findOrThrow(int id) {
        T item = this.repository.getById(id);
        if (item == null) throw new DoesNotExist("Object with id " + id);
    	return item;
    }
    
    public Collection<T> getAll() {
    	return this.repository.getAll();
    }
}

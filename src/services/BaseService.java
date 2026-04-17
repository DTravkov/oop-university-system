package services;

import java.io.Serializable;
import java.util.Collection;

import exceptions.DoesNotExist;
import model.Indexed;
import model.BaseRepository;

public abstract class BaseService<T extends Serializable & Indexed, R extends BaseRepository<T>> {
    
    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }
    
    public T findOrThrow(int id) {
        T item = this.repository.getById(id);
        if (item == null) throw new DoesNotExist();
    	return this.repository.getById(id);
    }
    
    public Collection<T> getAll() {
    	return this.repository.getAll();
    }
}
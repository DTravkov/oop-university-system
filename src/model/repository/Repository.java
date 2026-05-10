package model.repository;

import model.domain.SerializableModel;

import java.util.ArrayList;
import java.util.List;

public class Repository<T extends SerializableModel>{

    private static final Database DB = Database.getInstance();

    protected final Class<T> baseClass;
    protected final String baseName;

    public Repository(Class<T> baseClass) {
        this.baseClass = baseClass;
        this.baseName = this.getClass().getSimpleName().replace("Repository", "");
    }

    public T save(T entity) {
        return DB.save(baseClass, entity);
    }

    public void saveAll() {
        DB.saveAll();
    }

    public T update(T entity) {
        return DB.update(baseClass, entity);
    }


    public void delete(T entity) {
        DB.delete(baseClass, entity);
    }
    public void delete(int entityId) {
        DB.delete(baseClass, entityId);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return (List<T>) new ArrayList<>(DB.getAll(baseClass).values());
    }

    public boolean get(int entityId) {
        return DB.exists(baseClass, entityId);
    }

    public boolean get(T entity) {
        return DB.exists(baseClass, entity.getId());
    }

    public boolean exists(T entity) {
        return DB.exists(baseClass, entity);
    }

    public boolean exists(int entityId) {
        return DB.exists(baseClass, entityId);
    }


}

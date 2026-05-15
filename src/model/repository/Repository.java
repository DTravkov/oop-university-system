package model.repository;

import model.domain.SerializableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/**
 * this class is pretty interesting, because it provides CRUD and serialization for many objects at once.
 * we can pass any *.class variable to its constructor, and database will automatically create storage for this *.class
 * interesting fact, i made 3 refactors because i didnt like previous repository layers 
 */
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

    public void delete(T entity) {
        DB.delete(baseClass, entity);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return (List<T>) new ArrayList<>(DB.getAll(baseClass).values().stream().filter(e -> e.getId() > 0).toList());
    }

    public boolean get(int entityId) {
        return DB.exists(baseClass, entityId);
    }

    public boolean get(T entity) {
        return DB.exists(baseClass, entity.getId());
    }

    public T find(Predicate<T> query){
        return getAll().stream()
                        .filter(query)
                        .findFirst()
                        .orElse(null);
    }

    public List<T> findAll(Predicate<T> query){
        return getAll().stream()
                        .filter(query)
                        .toList();
    }

    public boolean exists(T entity) {
        return DB.exists(baseClass, entity);
    }

    public boolean exists(int entityId) {
        return DB.exists(baseClass, entityId);
    }


}

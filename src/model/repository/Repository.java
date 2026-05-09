package model.repository;

import model.domain.SerializableModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

public abstract class Repository<T extends SerializableModel> {

    private static final Database DB = Database.getInstance();

    protected final String baseName;

    public Repository() {
        this.baseName = this.getClass().getSimpleName().replace("Repository", "");
    }

    public T save(T entity) {
        return DB.save(entity.getClass(), entity);
    }

    public void update(T entity) {
        int id = entity.getId();
        if (entity.isNewRecord()) {
            throw new OperationNotAllowed("Cannot update: " + baseName + entity + " is write-only (id=0)");
        }
        this.find(id)
                .orElseThrow(() -> new DoesNotExist(baseName + " record with id=" + id));

        this.save(entity);
    }

    public void delete(int id) {
        DB.delete(bucketClass(), id);
    }

    protected Optional<T> findFirst(Predicate<T> predicate) {
        return DB.findFirst(bucketClass(), predicate);
    }

    protected List<T> findAll(Predicate<T> predicate) {
        return DB.findAll(bucketClass(), predicate);
    }

    protected boolean exists(Predicate<T> predicate) {
        return findFirst(predicate).isPresent();
    }

    public Optional<T> find(int id) {
        return DB.find(bucketClass(), id);
    }

    public List<T> findAll() {
        return DB.findAll(bucketClass());
    }

    public boolean exists(int id) {
        return DB.exists(bucketClass(), id);
    }

}

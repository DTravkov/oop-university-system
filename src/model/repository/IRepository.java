package model.repository;

import model.domain.SerializableModel;

import java.util.List;
import java.util.Optional;

public interface IRepository<T extends SerializableModel> {

    T save(T entity);

    void load();

    Optional<T> get(int id);

    List<T> getAll();

    void update(T entity, T newEntity);

    void delete(int id);

}

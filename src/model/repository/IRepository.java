package model.repository;

import model.domain.SerializableModel;

import java.util.List;
import java.util.Optional;

public interface IRepository<T extends SerializableModel> {

    T save(T entity);

    void load();

    Optional<T> find(int id);

    List<T> findAll();

    void update(T entity, T newEntity);

    void delete(int id);

    boolean exists(int id);

}

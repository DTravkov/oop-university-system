package model.repository;

import model.domain.SerializableModel;
import utils.IDGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Repository<T extends SerializableModel> implements IRepository<T> {

    private final String PATH;
    private IDGenerator idGenerator;
    protected Map<Integer, T> data = new HashMap<>();

    public Repository() {
        this.PATH = "serialized/" + this.getClass().getSimpleName() + ".ser";
        ensureFileExists();
        this.idGenerator = new IDGenerator(PATH);
        load();
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == 0) {
            entity.setId(idGenerator.nextId());
        }

        data.put(entity.getId(), entity);
        writeToFile();

        return entity;
    }

    @Override
    public void load() {
        File file = new File(PATH);

        if (!file.exists() || file.length() == 0) {
            this.data = new HashMap<>();
            return;
        }

        this.data = readFromFile();
    }

    @Override
    public void update(T entity, T newEntity) {
        if (!exists(entity.getId())) {
            throw new NoSuchElementException("Cannot update: Entity with ID " + entity.getId() + " not found");
        }
        int id = entity.getId();
        newEntity.setId(id);
        data.put(id, newEntity);
        writeToFile();
    }

    @Override
    public Optional<T> find(int id) {
        return Optional.ofNullable(data.get(id));
    }

    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(int id) {
        if (!exists(id)) {
            throw new NoSuchElementException("Cannot delete: Entity with ID " + id + " not found");
        }
        data.remove(id);
        writeToFile();
    }

    public boolean exists(int id) {
        return data.containsKey(id);
    }

    private void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH))) {
            oos.writeObject(this.data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + PATH, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, T> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            return this.data = (Map<Integer, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error reading from file: " + PATH, e);
        }
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(PATH);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage file: " + PATH, e);
        }
    }
}
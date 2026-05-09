package model.repository;

import model.domain.SerializableModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

public class Database {

    private static Database instance;

    private final String PATH;
    private final IDDatabase idDatabase;
    protected Map<Class<? extends SerializableModel>, Map<Integer, SerializableModel>> data = new HashMap<>();

    private Database() {
        this.PATH = "database.ser";
        ensureFileExists();
        this.idDatabase = IDDatabase.getInstance();
        load();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public <C extends SerializableModel> C save(Class<C> clazz, C entity) {
        if (entity.isNewRecord()) {
            entity.setId(idDatabase.nextId(clazz));
        }

        Map<Integer, SerializableModel> bucket = data.computeIfAbsent(clazz, k -> new HashMap<>());
        bucket.put(entity.getId(), entity);

        writeToFile();

        return entity;
    }

    public void load() {
        File file = new File(PATH);

        if (!file.exists() || file.length() == 0) {
            this.data = new HashMap<>();
            return;
        }

        this.data = readFromFile();
    }

    public <C extends SerializableModel> void update(Class<C> clazz, C entity) {
        int id = entity.getId();
        if (entity.isNewRecord()) {
            throw new OperationNotAllowed("Cannot update: " + clazz.getSimpleName() + " " + entity + " is write-only (id=0)");
        }
        find(clazz, id)
            .orElseThrow(() -> new DoesNotExist(clazz.getSimpleName() + " record with id=" + id));

        save(clazz, entity);
    }

    public <C extends SerializableModel> void delete(Class<C> clazz, int id) {
        find(clazz, id)
            .orElseThrow(() -> new DoesNotExist("Cannot delete: " + clazz.getSimpleName() + " with ID " + id + " not found"));
        Map<Integer, SerializableModel> bucket = data.get(clazz);
        if (bucket != null) {
            bucket.remove(id);
        }
        writeToFile();
    }

    @SuppressWarnings("unchecked")
    protected <C extends SerializableModel> Optional<C> findFirst(Class<C> clazz, Predicate<C> predicate) {
        Map<Integer, SerializableModel> bucket = data.get(clazz);
        if (bucket == null) {
            return Optional.empty();
        }
        return bucket.values().stream()
                .map(e -> (C) e)
                .filter(predicate)
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    protected <C extends SerializableModel> List<C> findAll(Class<C> clazz, Predicate<C> predicate) {
        Map<Integer, SerializableModel> bucket = data.get(clazz);
        if (bucket == null) {
            return List.of();
        }
        return bucket.values().stream()
                .map(e -> (C) e)
                .filter(predicate)
                .toList();
    }

    protected <C extends SerializableModel> boolean exists(Class<C> clazz, Predicate<C> predicate) {
        return findFirst(clazz, predicate).isPresent();
    }

    @SuppressWarnings("unchecked")
    public <C extends SerializableModel> Optional<C> find(Class<C> clazz, int id) {
        Map<Integer, SerializableModel> bucket = data.get(clazz);
        if (bucket == null) {
            return Optional.empty();
        }
        SerializableModel entity = bucket.get(id);
        return Optional.ofNullable((C) entity);
    }

    public <C extends SerializableModel> List<C> findAll(Class<C> clazz) {
        return findAll(clazz, c -> true);
    }

    public <C extends SerializableModel> boolean exists(Class<C> clazz, int id) {
        return find(clazz, id).isPresent();
    }

    private void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH))) {
            oos.writeObject(this.data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + PATH, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Class<? extends SerializableModel>, Map<Integer, SerializableModel>> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            return (Map<Class<? extends SerializableModel>, Map<Integer, SerializableModel>>) ois.readObject();
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

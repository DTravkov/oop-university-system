package model.repository;

import model.domain.SerializableModel;
import settings.AppSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Database {

    private static Database instance;

    private final String PATH;
    private final IDDatabase idDatabase;
    protected Map<Class<? extends SerializableModel>, Map<Integer, SerializableModel>> data = new HashMap<>();

    private Database() {
        this.PATH = AppSettings.DEFAULT_DATA_DIRECTORY + "database.ser";
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

    public void saveAll(){
        writeToFile();
    }
    
    public <E extends SerializableModel> E save(Class<? extends SerializableModel> className, E entity) {
        if (entity.isNewRecord()) {
            entity.setId(idDatabase.nextId(className));
        }

        Map<Integer, SerializableModel> bucket = data.computeIfAbsent(className, k -> new HashMap<>());
        bucket.put(entity.getId(), entity);

        writeToFile();

        return entity;
    }

    public Map<Integer, SerializableModel> getAll(Class<? extends SerializableModel> className) {
        Map<Integer, SerializableModel> bucket = data.computeIfAbsent(className, k -> new HashMap<>());
        return bucket;
    }

    public <E extends SerializableModel> E update(Class<? extends SerializableModel> className, E entity) {
        if (entity.isNewRecord()) {
            throw new RuntimeException(entity.getClass().getSimpleName() + " cannot update without id");
        }

        Map<Integer, SerializableModel> bucket = data.computeIfAbsent(className, k -> new HashMap<>());
        if (bucket.get(entity.getId()) == null) {
            throw new RuntimeException(entity.getClass().getSimpleName() + " object with id=" + entity.getId() + " does not exist");
        }

        bucket.put(entity.getId(), entity);

        writeToFile();

        return entity;
    }

    public <E extends SerializableModel> void delete(Class<? extends SerializableModel> className, E entity) {
        this.delete(className, entity.getId());
    }

    public void delete(Class<? extends SerializableModel> className, int entityId) {
        Map<Integer, SerializableModel> bucket = data.get(className);
        if (bucket != null && bucket.remove(entityId) != null) {
            writeToFile();
        }
    }

    public <E extends SerializableModel> boolean exists(Class<? extends SerializableModel> className, E entity) {
        return exists(className, entity.getId());
    }

    public boolean exists(Class<? extends SerializableModel> className, int entityId) {
        Map<Integer, SerializableModel> bucket = data.get(className);
        return bucket != null && bucket.get(entityId) != null;
    }

    private SerializableModel requireEntity(Class<? extends SerializableModel> className, int entityId) {
        Map<Integer, SerializableModel> bucket = data.get(className);
        if (bucket == null) {
            throw new RuntimeException("No records for type " + className.getSimpleName());
        }
        SerializableModel entity = bucket.get(entityId);
        if (entity == null) {
            throw new RuntimeException(className.getSimpleName() + " object with id=" + entityId + " does not exist");
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <E extends SerializableModel> Optional<E> find(Class<? extends SerializableModel> className, int entityId) {
        Map<Integer, SerializableModel> bucket = data.get(className);
        if (bucket == null) {
            return Optional.empty();
        }
        SerializableModel entity = bucket.get(entityId);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of((E) entity);
    }



    private void load() {
        File file = new File(PATH);

        if (!file.exists() || file.length() == 0) {
            this.data = new HashMap<>();
            return;
        }

        this.data = readFromFile();
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

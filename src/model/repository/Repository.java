package model.repository;

import model.domain.SerializableModel;
import utils.IDGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Repository<T extends SerializableModel> {

    private final String PATH;
    private IDGenerator idGenerator;
    protected Map<Integer, T> data = new HashMap<>();

    public Repository() {
        this.PATH = "serialized/" + this.getClass().getSimpleName() + ".ser";
        ensureFileExists();
        this.idGenerator = new IDGenerator(PATH);
        load();
    }

    public T save(T entity) {
        // if entity's id == 0, it means that the enitity is new,.
        // therefore, it needs unique id, that generator provides
        // only negative id that should appera in code is -1, it is reserved for deleted user.
        if (entity.getId() == 0) {
            entity.setId(idGenerator.nextId());
        }

        data.put(entity.getId(), entity);
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

    
    public void update(T entity, T newEntity) {
        if (!exists(e -> e.getId() == entity.getId())) {
            throw new NoSuchElementException("Cannot update: Entity with ID " + entity.getId() + " not found");
        }
        int id = entity.getId();
        newEntity.setId(id);
        data.put(id, newEntity);
        writeToFile();
    }

    
    public void delete(int id) {
        if (!exists(e -> e.getId() == id)) {
            throw new NoSuchElementException("Cannot delete: Entity with ID " + id + " not found");
        }
        data.remove(id);
        writeToFile();
    }


    

    protected Optional<T> findFirst(Predicate<T> predicate) {
        return this.data.values().stream()
                                 .filter(predicate)
                                 .findFirst();
    }

    protected List<T> findAll(Predicate<T> predicate) {
        return this.data.values().stream()
                                 .filter(predicate)
                                 .toList();
    }

    
    protected boolean exists(Predicate<T> predicate) {
        return findFirst(predicate).isPresent();
    }





    public T find(int id){
        return findFirst(entity -> entity.getId() == id).orElse(null);
    }

    public List<T> findAll(){
        return findAll(enitity -> true);
    }

    public boolean exists(int id){
        return findFirst(entity -> entity.getId() == id).isPresent();
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
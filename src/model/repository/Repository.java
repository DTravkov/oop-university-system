package model.repository;

import model.domain.SerializableModel;
import utils.IDGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

import exceptions.DoesNotExist;
import exceptions.OperationNotAllowed;

public class Repository<T extends SerializableModel> {

    private final String PATH;
    private IDGenerator idGenerator;
    protected Map<Integer, T> data = new HashMap<>();
    protected final String baseName;

    public Repository() {
        this.baseName = this.getClass().getSimpleName().replace("Repository", "");
        this.PATH = "serialized/" + baseName + ".ser";
        ensureFileExists();
        this.idGenerator = new IDGenerator(PATH);
        load();
    }

    public T save(T entity) {
        // upsert pattern save function.
        // if entity is new(id == 0), then save() creates one
        // else it will update record with id == entity.getId()
        if (entity.isNewRecord()) {
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

    
    public void update(T entity){
        // used to save changes of ALREADY existing object.
        // this will not throw any excpetions if entity is fetched and edited object's id exists in repository.data map
        int id = entity.getId();
        if(entity.isNewRecord()) 
            throw new OperationNotAllowed("Cannot update: " + baseName + entity.toString() + " is write-only (id=0)");
        this.find(id)
            .orElseThrow(() -> new DoesNotExist(baseName + " record with id=" + id));

        this.save(entity);
    }

    
    public void delete(int id) {
        this.find(id)
            .orElseThrow(() -> new DoesNotExist("Cannot delete: " + baseName + " with ID " + id + " not found"));
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


    public Optional<T> find(int id){
        return findFirst(entity -> entity.getId() == id);
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
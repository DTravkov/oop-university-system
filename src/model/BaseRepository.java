package model;

import java.io.*;
import java.util.*;

public abstract class BaseRepository<T extends Serializable & Indexed> {
    
    protected abstract String getFilePath();
    
    protected Set<T> data = new HashSet<>();
    
    protected int globalId;

    public BaseRepository() {
        load();
        globalId = data.stream().map(elem->elem.getId()).max(Integer::compare).orElse(0)+1;
    }

    public void save() {
        File file = new File(getFilePath());
        
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving/creating file " + getFilePath() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File file = new File(getFilePath());
        
        if (!file.exists()) {
            return;
        }

        if (file.length() == 0) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object readData = ois.readObject();
            if (readData instanceof Set) {
                data = (Set<T>) readData;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + getFilePath() + ": " + e.getMessage());
        }
    }

    public void add(T item) {
    	item.setId(globalId++);
        data.add(item);
        save();
    }
    
    public void delete(T item) {
        data.remove(item);
        save();
    }
    
    public void update(T item, T newItem) {
    	newItem.setId(item.getId());
        data.remove(item);
        data.add(newItem);
        save();
    }
    
    public T getById(int id) {
    	T item = data.stream().filter(elem -> elem.getId() == id).findFirst().orElse(null);
    	return item;
    }
    
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(data);
    }
}
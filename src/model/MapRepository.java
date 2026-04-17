package model;

import java.io.*;
import java.util.*;

public abstract class MapRepository<T extends Serializable & Indexed> {
    
    protected abstract String getFilePath();
    
    protected HashMap<Integer, HashSet<T>> data = new HashMap<>();
    
    protected static int globalId;

    public MapRepository() {
        load();
        globalId = data.values().stream()
                .flatMap(set -> set.stream())
                .mapToInt(e -> e.getId())
                .max()
                .orElse(0);
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
            if (readData instanceof HashMap) {
                data = (HashMap<Integer,HashSet<T>>) readData;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + getFilePath() + ": " + e.getMessage());
        }
    }

    public void add(int ownerId, T item) {
    	globalId++;
    	item.setId(globalId);
    	if(data.get(ownerId) == null) data.put(ownerId, new HashSet<T>());
    	data.get(ownerId).add(item);
        save();
    }
    
    public void delete(int ownerId, T item) {
    	HashSet<T> set = data.get(ownerId);
    	if(set == null || set.contains(item) == false) return;
    	set.remove(item);
        save();
    }
    
    public void update(int ownerId, T item, T newItem) {
    	HashSet<T> list = data.get(ownerId);
    	if(list == null || list.contains(item) == false) return;
    	
    	newItem.setId(item.getId());
    	
        list.remove(item);
        list.add(newItem);
        save();
    }
    
    public T getValueById(int id) {
        Collection<T> list = this.getAll();
        if(list == null) return null;
        return list.stream().filter(elem -> elem.getId() == id).findFirst().orElse(null);
    }
    
    public Collection<T> getAllByOwnerId(int ownerId) {
    	HashSet<T> set = data.get(ownerId);
        return set != null ? Collections.unmodifiableSet(set) : new HashSet<>();
    }
    
    public Collection<T> getAll() {
    	HashSet<T> allEntries = new HashSet<>();
        for (HashSet<T> userEntries : data.values()) {
            allEntries.addAll(userEntries);
        }
        return Collections.unmodifiableCollection(allEntries);
    }
}
package model.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import model.domain.SerializableModel;
import settings.AppSettings;

/**
 * This class separates id generation from {@link Database}
 * Its the best choice, because this class can generate ids quickly (without looking at any data, that we already saved)
 * Also, IDDatabase shares responsibility, so {@link Database} is cleaner
 */

public class IDDatabase {

    private static IDDatabase instance;

    private final String path;
    private Map<Class<? extends SerializableModel>, Integer> idData;

    private IDDatabase(String path) {
        this.path = path;
        createFile();
        this.idData = load();
    }

    public static IDDatabase getInstance() {
        if (instance == null) {
            instance = new IDDatabase(AppSettings.DEFAULT_DATA_DIRECTORY + "id_database.ser");
        }
        return instance;
    }

    public int nextId(Class<? extends SerializableModel> className) {
        int next = idData.getOrDefault(className, 0) + 1;
        idData.put(className, next);
        writeToFile();
        return next;
    }

    private void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(idData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write ID database: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Class<? extends SerializableModel>, Integer> load() {
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object obj = ois.readObject();
            return (Map<Class<? extends SerializableModel>, Integer>) obj;
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void createFile() {
        try {
            Path pathObj = Paths.get(path);
            if (pathObj.getParent() != null) {
                Files.createDirectories(pathObj.getParent());
            }
            if (Files.notExists(pathObj)) {
                Files.createFile(pathObj);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize ID database file: " + path, e);
        }
    }
}

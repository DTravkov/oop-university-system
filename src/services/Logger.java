package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import model.domain.User;
import settings.AppSettings;

public class Logger {

    private final String PATH;
    protected Map<Integer, List<String>> data = new HashMap<>();
    protected final String baseName;

    public Logger() {
        this.baseName = this.getClass().getSimpleName();
        this.PATH = AppSettings.DEFAULT_DATA_DIRECTORY + baseName + ".ser";
        ensureFileExists();
        load();
    }

    public void log(String logRecord) {
        User active = AppSettings.getActiveUser();
        int activeUserId = active != null ? active.getId() : 0;
        List<String> logs = data.get(activeUserId);
        if (logs == null) logs = data.put(activeUserId, new ArrayList<>());
        logs.add(logRecord);
        writeToFile();
    }

    public void load() {
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
    private Map<Integer, List<String>> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            return (Map<Integer, List<String>>) ois.readObject();
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

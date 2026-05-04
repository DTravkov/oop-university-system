package utils;

import model.domain.User;
import settings.AppSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Logger {

    private static final Logger INSTANCE = new Logger();

    private final String path = AppSettings.DEFAULT_REPOSITORY_ROOT + "Logger.ser";
    private List<LogEntry> data = new ArrayList<>();
    private boolean isActive = true;

    private Logger() {
        ensureFileExists();
        load();
    }

    public static void setIsActive(boolean isActive){
        INSTANCE.isActive = isActive;
    }

    public static void log(String action) {
        if (!INSTANCE.isActive) {
            return;
        }
        User user = AppSettings.getActiveUser();
        LogEntry entry = new LogEntry(action, user);
        INSTANCE.data.add(entry);
        INSTANCE.writeToFile();
    }

    public static List<LogEntry> getUserLogs(int userId) {
        List<LogEntry> userLogs = INSTANCE.data.stream().filter(log -> log.getUserId() == userId).toList();
        return List.copyOf(userLogs);
    }
    
    public static List<LogEntry> getAllLogs() {
        return List.copyOf(INSTANCE.data);
    }

    private void load() {
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            this.data = new ArrayList<>();
            return;
        }
        this.data = readFromFile();
    }

    private void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this.data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<LogEntry> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (List<LogEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error reading from file: " + path, e);
        }
    }

    private void ensureFileExists() {
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            if (Files.notExists(p)) {
                Files.createFile(p);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage file: " + path, e);
        }
    }
}

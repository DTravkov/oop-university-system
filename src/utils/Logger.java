package utils;

import model.domain.User;
import settings.AppSettings;
import settings.SessionData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Logger {

    private static final Logger INSTANCE = new Logger();

    private final String path = AppSettings.DEFAULT_REPOSITORY_ROOT + "Logger.ser";
    private Map<Integer, List<LogEntry>> data = new HashMap<>();
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
        User user = SessionData.getInstance().getUser();
        if(user == null) return;
        int userId = user.getId();
        LogEntry entry = new LogEntry(action, user.getId());
        INSTANCE.data.computeIfAbsent(userId, logList -> new ArrayList<>()).add(entry);
        INSTANCE.writeToFile();
    }

    public static List<LogEntry> getUserLogs(int userId) {
        List<LogEntry> userLogs = INSTANCE.data.get(userId);
        if (userLogs == null) {
            return List.of();
        }
        return List.copyOf(userLogs);
    }
    
    public static Map<Integer, List<LogEntry>> getAllLogs() {
        return Map.copyOf(INSTANCE.data);
    }

    private void load() {
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            this.data = new HashMap<>();
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
    private Map<Integer, List<LogEntry>> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (Map<Integer, List<LogEntry>>) ois.readObject();
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

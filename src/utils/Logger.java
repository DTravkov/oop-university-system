package utils;

import model.domain.SerializableModel;
import model.domain.User;
import settings.AppSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Logger is a singleton used by services to log user actions in the system.
 * Data is logged using {@link LogEntry} subclass.
 * Logger should be used in servics.
 */
public final class Logger {

    private static final Logger INSTANCE = new Logger();

    private final String path = AppSettings.DEFAULT_DATA_DIRECTORY + "Logger.ser";
    private List<LogEntry> data = new ArrayList<>();
    
    private boolean isActive = true;
    private static final int RECENT_LOG_HOURS = AppSettings.RECENT_LOG_HOURS;

    private Logger() {
        createFile();
        load();
    }

    // PUBLIC

    public static void setIsActive(boolean isActive){
        INSTANCE.isActive = isActive;
    }

    public static void log(String action) {
        if (!INSTANCE.isActive) return;

        User user = AppSettings.getActiveUser();
        LogEntry entry = new LogEntry(action, user);

        INSTANCE.data.add(entry);
        INSTANCE.writeToFile();
    }



    public static List<LogEntry> getUserLogs(int userId) {
        List<LogEntry> userLogs = INSTANCE.data.stream().filter(log -> log.getUserId() == userId).toList();
        return List.copyOf(userLogs);
    }

    public static List<LogEntry> getRecentLogs() {
        List<LogEntry> logs = getAllLogs();
        Instant startingPoint = Instant.now().minus(Duration.ofHours(RECENT_LOG_HOURS));
        return logs.stream()
                   .filter(log -> log.getTime().toInstant().isAfter(startingPoint))
                   .toList();
    }
    
    public static List<LogEntry> getAllLogs() {
        return List.copyOf(INSTANCE.data);
    }




    // FILE 

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

    private void createFile() {
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


    /**
     * LogEntry stores user+time+action of a user in the system.
     * Implemented in base service, so each action is logged
     */
    public static final class LogEntry extends SerializableModel {
        private static final long serialVersionUID = 1L;

        public final Date time = new Date();
        public final String action;
        public final int userId;
        public final String fullname;

        public LogEntry(String action, User user){
            this.action = action;
            this.userId = user.getId();
            this.fullname = user.getFullname();
        }

        public Date getTime() {
            return time;
        }

        public String getAction() {
            return action;
        }

        public int getUserId() {
            return userId;
        }

        public String getFullname() {
            return fullname;
        }

        @Override
        public String asLine() {
            String a = action == null ? "" : action;
            return String.format("%s | ID: %d | %s | %s", StringUtils.formatLogTime(time), userId, fullname, a);
        }

        @Override
        public String asTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("Time: ").append(StringUtils.formatLogTime(time)).append('\n');
            sb.append("User id: ").append(userId).append('\n');
            sb.append("Full name: ").append(fullname).append('\n');
            sb.append("Action:\n").append(action == null ? "" : action).append('\n');
            return sb.toString();
        }

        @Override
        public String toString() {
            String a = action == null ? "" : action;
            return StringUtils.formatLogTime(time) + " | " + fullname +", id=" + userId + " | " + a;
        }
    }
}

package utils;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private final String idPath;
    private AtomicInteger lastId;

    public IDGenerator(String idPath) {
        this.idPath = idPath.replace(".ser", "") + "_id.ser";
        this.lastId = load();
    }

    public synchronized int nextId() {
        int next = lastId.incrementAndGet();
        save(lastId);
        return next;
    }

    private void save(AtomicInteger id) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(idPath))) {
            oos.writeObject(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AtomicInteger load() {
        File file = new File(idPath);

        if (!file.exists() || file.length() == 0) {
            return new AtomicInteger(0);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (AtomicInteger) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AtomicInteger(0);
        }
    }
}
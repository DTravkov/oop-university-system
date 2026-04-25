package services.events;

import java.util.function.Consumer;

public interface ISubject {
    void subscribe(Consumer<Event> handler);
    void unsubscribe(Consumer<Event> handler);
    void publish(Event event);
}

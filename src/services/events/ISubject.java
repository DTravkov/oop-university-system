package services.events;

public interface ISubject {
    void subscribe(IObserver observer);
    void unsubscribe(IObserver observer);
    void publish(Event event);
}

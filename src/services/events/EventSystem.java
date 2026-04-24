package services.events;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;

import java.util.ArrayList;
import java.util.List;


public class EventSystem implements ISubject{


    private static List<IObserver> subscribers = new ArrayList<>();
    private static EventSystem instance = new EventSystem();
    
    private EventSystem(){}

    @Override
    public void subscribe(IObserver observer) {
        if(observer == null) 
            throw new NullPointerException("Cannot subscribe null observer");
        if(subscribers.contains(observer))
            throw new AlreadyExists(" subscriber in EventSystem");
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(IObserver observer) {
        if(observer == null) 
            throw new NullPointerException("Cannot unsubscribe null observer");
        if(!subscribers.contains(observer))
            throw new DoesNotExist(" subscriber in EventSystem");
        subscribers.remove(observer);
    }

    @Override
    public void publish(Event updateData) {
        for(IObserver subscriber : subscribers){
            try{
                subscriber.update(updateData);
            }
            catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
            
    }

    public static EventSystem getInstance() {
        if(instance == null) instance = new EventSystem();
        return instance;
    }

}

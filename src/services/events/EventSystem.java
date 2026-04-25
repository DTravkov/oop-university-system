package services.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import utils.FieldValidator;


public class EventSystem {

    private static final EventSystem INSTANCE = new EventSystem();
    private final Map<Class<? extends Event>, List<Consumer<Event>>> handlers = new HashMap<>();

    private EventSystem() {}

    public <T extends Event> void subscribe(Class<T> eventType, Consumer<T> handler) {
        FieldValidator.requireNonNull(eventType, "Event type is required");
        FieldValidator.requireNonNull(handler, "Handler is required");


        @SuppressWarnings("unchecked")
        Consumer<Event> eventHandler = (Consumer<Event>) handler;
        
        if(handlers.get(eventType) == null){
            handlers.put(eventType, new ArrayList<>());
        }
        List<Consumer<Event>> list = handlers.get(eventType);
        list.add(eventHandler);


    }

    public void publish(Event event) {
        FieldValidator.requireNonNull(event, "Event is required");
        List<Consumer<Event>> list = handlers.get(event.getClass());
        if(list == null || list.isEmpty()){
            return;
        }
        for(Consumer<Event> handler : handlers.get(event.getClass())){
            if(handler != null){
                handler.accept(event);
            }
            
        }
        
    }

    public static EventSystem getInstance() {
        return INSTANCE;
    }
}

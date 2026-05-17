package services.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import services.events.interfaces.Event;
import utils.FieldValidator;

/**
 * My favourite pattern)
 * Used by services to react to events, e.g Employee deletion leads to clean up of his messenger data.
 * It was insired by Express.js event system, global in every service.
 */

public class EventSystem {

    private static final EventSystem INSTANCE = new EventSystem();
    private final Map<Class<? extends Event>, List<Consumer<Event>>> handlers = new HashMap<>();

    private EventSystem() {}

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventType, Consumer<T> handler) {
        if(handlers.get(eventType) == null){
            handlers.put(eventType, new ArrayList<>());
        }
        // generic to concrete event cast, because map stores list of callbakcs with concrete types
        handlers.get(eventType).add((Consumer<Event>) handler);

    }
    
    public void publish(Event event) {
        FieldValidator.requireNonNull(event, "Event");
        //event.getClass() because keys of map are actually class literals
        List<Consumer<Event>> handlerList = handlers.get(event.getClass());
        if(handlerList == null || handlerList.isEmpty()){
            return;
        }

        for(var handler : handlerList){
            if(handler != null){
                handler.accept(event);
            }
        }
        
    }

    public static EventSystem getInstance() {
        return INSTANCE;
    }

}

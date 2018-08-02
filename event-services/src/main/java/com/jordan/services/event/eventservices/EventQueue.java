package com.jordan.services.event.eventservices;

import com.jordan.services.event.eventservices.model.Event;
import com.jordan.services.event.eventservices.repositories.EventRepository;
import com.jordan.services.event.eventservices.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jordan on 4/17/18.
 */
@Component
@Scope("prototype")
public class EventQueue implements Runnable {
    private BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(1024);

    @Autowired
    private EventRepository eventRepository;

    //Spring makes its own threads everytime you make a request call to the controller so I need to add this to synch blcok
    public void addToQueue(Event event){
        synchronized (this) {
            try {
                eventQueue.put(event);
                this.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Event seeNextItem(){
        return eventQueue.peek();
    }

    public boolean isEmpty(){
        return eventQueue.isEmpty();
    }

    //we start a thread from startup of node and this thread keeps running always waiting for something in the queue
    @Override
    public void run() {
        synchronized (this) {
            boolean running = true;
            while (running) {
                try {
                    if (eventQueue.isEmpty()) {
                        this.wait();
                    } else {
                        if (!eventRepository.existsByTimestamp(eventQueue.peek().getTimestamp() - 1)) {
                            Event e = eventQueue.take();
                            eventRepository.save(e);
                        }
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    System.out.println("something happened to event queue thread");
                }
            }
        }
    }
}

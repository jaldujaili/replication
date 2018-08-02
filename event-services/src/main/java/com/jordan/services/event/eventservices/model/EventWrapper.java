package com.jordan.services.event.eventservices.model;

import java.util.List;

/**
 * Created by jordan on 4/22/18.
 */

//needed to send the json as a list
public class EventWrapper {
    private List<Event> events;

    public EventWrapper(List<Event> events) {
        this.events = events;
    }

    protected  EventWrapper(){

    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}

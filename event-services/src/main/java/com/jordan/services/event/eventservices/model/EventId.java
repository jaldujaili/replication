package com.jordan.services.event.eventservices.model;

/**
 * Created by jordan on 4/11/18.
 */
public class EventId {
    private Integer eventid;

    public EventId(Integer eventid) {
        this.eventid = eventid;
    }

    protected EventId() {

    }
    public Integer getEventid() {
        return eventid;
    }

    public void setEventid(Integer eventid) {
        this.eventid = eventid;
    }
}

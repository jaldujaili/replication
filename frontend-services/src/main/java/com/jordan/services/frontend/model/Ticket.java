package com.jordan.services.frontend.model;

/**
 * Created by jordan on 3/30/18.
 */
public class Ticket {
    private int eventid;
    private int tickets;
    private int targetuser;

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public int getTargetuser() {
        return targetuser;
    }

    public void setTargetuser(int targetuser) {
        this.targetuser = targetuser;
    }
}

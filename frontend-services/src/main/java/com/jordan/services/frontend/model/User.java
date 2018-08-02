package com.jordan.services.frontend.model;

import com.jordan.services.frontend.model.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordan on 3/29/18.
 */
public class User {
    private int userid;
    private String username;
    private List<Event> tickets;
    private int eventid;
    private int targetuser;

    public User(int userid, String username) {
        this.userid = userid;
        this.username = username;
        this.tickets = new ArrayList<Event>();
    }
    protected User(){

    }

    public User(int userid, String username, List<Event> tickets) {
        this.userid = userid;
        this.username = username;
        this.tickets = tickets;
    }

    public void addATicket(Event ticket){
        this.tickets.add(ticket);
    }
    public int getUserId() {
        return userid;
    }

    public void setUserId(int id) {
        this.userid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Event> getTickets() {
        return tickets;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getTargetuser() {
        return targetuser;
    }

    public void setTargetuser(int targetuser) {
        this.targetuser = targetuser;
    }
}

package com.jordan.services.frontend.model;


import com.jordan.services.frontend.model.Event;

import java.util.List;

/**
 * Created by jordan on 3/30/18.
 */
public class UserEvent {
    private int userid;
    private String username;
    private List<Event> tickets;

    public UserEvent(int userid, String username, List<Event> tickets) {
        this.userid = userid;
        this.username = username;
        this.tickets = tickets;
    }

    protected UserEvent(){

    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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

    public void setTickets(List<Event> tickets) {
        this.tickets = tickets;
    }
}

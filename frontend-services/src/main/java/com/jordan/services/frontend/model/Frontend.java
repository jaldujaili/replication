package com.jordan.services.frontend.model;

import com.fasterxml.jackson.annotation.JsonView;


/**
 * Created by jordan on 3/29/18.
 */
public class Frontend {
    private int eventid;

    private String eventname;
    private int userid;
    private int avail;
    private int purchased;
    private int numtickets;
    private String username;
    private int targetuser;
    private int tickets;


    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    public int getPurchased() {
        return purchased;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
    }

    public int getNumtickets() {
        return numtickets;
    }

    public void setNumtickets(int numtickets) {
        this.numtickets = numtickets;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTargetuser() {
        return targetuser;
    }

    public void setTargetuser(int targetuser) {
        this.targetuser = targetuser;
    }
}

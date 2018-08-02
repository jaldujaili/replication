package com.jordan.services.frontend.model;

/**
 * Created by jordan on 3/30/18.
 */
public class EventWOTickets {
    private int eventid;
    private String eventname;
    private int avail;
    private int purchase;
    private int userid;

    public EventWOTickets(int eventid, String eventname, int avail, int purchase, int userid) {
        this.eventid = eventid;
        this.eventname = eventname;
        this.avail = avail;
        this.purchase = purchase;
        this.userid = userid;
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

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    public int getPurchase() {
        return purchase;
    }

    public void setPurchase(int purchase) {
        this.purchase = purchase;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}

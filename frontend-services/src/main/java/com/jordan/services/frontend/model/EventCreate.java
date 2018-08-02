package com.jordan.services.frontend.model;

/**
 * Created by jordan on 4/4/18.
 */
public class EventCreate {
    private Integer userid;
    private Integer numtickets;
    private String eventname;

    public EventCreate(Integer userid, Integer numtickets, String eventname) {
        this.userid = userid;
        this.numtickets = numtickets;
        this.eventname = eventname;
    }

    protected EventCreate(){

    }


    public Integer getUserid() {
        return userid;
    }
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getNumtickets() {
        return numtickets;
    }

    public void setNumtickets(Integer numtickets) {
        this.numtickets = numtickets;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }
}

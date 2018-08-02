package com.jordan.services.event.eventservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Created by jordan on 3/16/18.
 */


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    @Id
    @GeneratedValue
    private  Integer eventid;

    @Column
    private String eventname;

    @Column
    private Integer avail;

    @Column
    private Integer purchased;

    @Column
    private Integer userid;

    @Column
    private Integer timestamp;

    private Integer tickets;
    private Integer numtickets;

    public Event(String eventname, Integer avail, Integer userid) {
        this.eventname = eventname;
        this.avail = avail;
        this.userid = userid;
        this.purchased = 0;
    }
    public Event(Integer eventid, String eventname, Integer avail, Integer userid) {
        this.eventid = eventid;
        this.eventname = eventname;
        this.avail = avail;
        this.userid = userid;
        this.purchased = 0;
    }

    protected Event(){

    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTickets() {
        return tickets;
    }

    public void setTickets(Integer tickets) {
        this.tickets = tickets;
    }

    public Integer getNumTickets(){return numtickets;}

    public void setNumtickets(Integer numtickets) {
        this.numtickets = numtickets;
    }

    public void setPurchase(Integer tickets){
        this.purchased = this.purchased + tickets;
        this.avail = this.avail - tickets;
    }

    public Integer getUserid() {return userid;}

    public void setUserid(Integer userid) {this.userid = userid;}

    public Integer getEventid() {
        return eventid;
    }

    public void setEventid(Integer eventid) {
        this.eventid = eventid;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public Integer getAvail() {
        return avail;
    }

    public void setAvail(Integer avail) {
        this.avail = avail;
    }

    public Integer getPurchased() {
        return purchased;
    }

    public void setPurchased(Integer purchased) {
        this.purchased = purchased;
    }
}


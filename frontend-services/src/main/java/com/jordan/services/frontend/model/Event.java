package com.jordan.services.frontend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by jordan on 3/30/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @JsonView(View.Summary.class)
    private  Integer eventid;

    @JsonView(View.SummaryRest.class)
    private String eventname;

    @JsonView(View.SummaryRest.class)
    private Integer avail;

    @JsonView(View.SummaryRest.class)
    private Integer purchased;

    @JsonView(View.SummaryRest.class)
    private Integer userid;

    private Integer tickets;
    private Integer numtickets;

    public Event(String eventname, Integer avail, Integer userid) {
        this.eventname = eventname;
        this.avail = avail;
        this.userid = userid;
        this.purchased = 0;
    }

    protected Event(){

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


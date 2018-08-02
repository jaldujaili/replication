package com.jordan.services.event.eventservices.services;

import com.google.gson.Gson;
import com.jordan.services.event.eventservices.EventQueue;
import com.jordan.services.event.eventservices.model.EventWrapper;
import com.jordan.services.event.eventservices.model.Member;
import com.jordan.services.event.eventservices.model.User;
import com.jordan.services.event.eventservices.repositories.EventRepository;
import com.jordan.services.event.eventservices.model.Event;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jordan on 3/16/18.
 */

@Service
public class EventService {
    private EventRepository eventRepository;
    private AtomicInteger timestamp = new AtomicInteger();
//    String UserNode =  "http://localhost:4454/";
    String UserNode =  "http://mc01:2000/";

    @Autowired
    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    EventQueue eventQueue;

    public Event createEvent(String eventname, int numtickets, int userid){
        if(!eventRepository.existsByEventname(eventname)){
            Event event = new Event(eventname, numtickets, userid);
            return eventRepository.save(event);
        }
        return null;
    }

    //create the event with id for the follwer nodes to make sure they are in the same place on the database
    public Event createEventWId(int eventid, String eventname, int avail, int userid){
        if(!eventRepository.existsById(eventid)){
            Event event = new Event(eventid, eventname, avail, userid);
            return eventRepository.save(event);
        }
        return null;
    }

    public Event validateEvent(int id) throws NoSuchElementException {
        Event ev = this.eventRepository.findByEventid(id);
        if(ev == null){
            throw new NoSuchElementException("event does not exist" + id);
        }
        return ev;
    }

    //this creates the purchased event
    public synchronized Event purchase(int id, Event input){
        Event ev = validateEvent(id);
        if (ev.getAvail() >= input.getTickets()){
            ev.setPurchase(input.getTickets());
            buyingTickets(id,input.getTickets(), input.getUserid());
            eventRepository.save(ev);
            return createTimestamp(ev ,input.getUserid());
        }else{
            throw new NoSuchElementException("can't purchase");
        }
    }

    //a request to the user when creating tickets
    private void buyingTickets(int eventid, int tickets, int userid){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonRequest = new JSONObject();

        try {
            jsonRequest.put("eventid", String.valueOf(eventid));
            jsonRequest.put("tickets", String.valueOf(tickets));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new NoSuchElementException("");
        }

        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
        try {
            ResponseEntity<User> userResponse = this.restTemplate.exchange(
                    UserNode+ String.valueOf(userid)+"/tickets/add",
                    HttpMethod.POST, request, new ParameterizedTypeReference<User>() {
                    });
        }catch(Exception e){
            throw new NoSuchElementException("user does not exist " + userid);
        }
    }

    public List<Event> getEvents() {
        return (List<Event>) this.eventRepository.findAll();
    }

    //this either adds the event to queue or memory. it first checks to see if the previous timestamps is present, if
    //yes save, else add to queue
    public void addEventToQueue(Event event){
        if(!eventQueue.isEmpty()) {//checks to see if the queue is empty, so we dont have to compare events
            if (event.getTimestamp() == 1 || (eventRepository.existsByTimestamp(event.getTimestamp() - 1)
                    && event.getTimestamp() < eventQueue.seeNextItem().getTimestamp())) {
                eventRepository.save(event);
            } else {
                eventQueue.addToQueue(event);
            }
        }else{
            if (event.getTimestamp() == 1 || (eventRepository.existsByTimestamp(event.getTimestamp() - 1))){
                eventRepository.save(event);
            }else{
                eventQueue.addToQueue(event);
            }
        }
    }

    //This is called by transitionedLeadership() and the register in the membership controller. this duplicates all the events of the leader and makes a request that
    //send all data to other nodes.
    public void sendSnapshot(Member member) throws URISyntaxException{
        List<Event> events = (List<Event>) eventRepository.findAll();
        URI uri=new URI("http", null, member.getIp(), member.getPort(),"/updateEvents", null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try{
            EventWrapper ew = new EventWrapper(events);
            Gson gson = new Gson();
            String json = gson.toJson(ew);
            HttpEntity<String> request = new HttpEntity<String>(json, headers);

            ResponseEntity<List<Event>> eventResponse = this.restTemplate.exchange(
                    uri,
                    HttpMethod.POST, request, new ParameterizedTypeReference<List<Event>>() {
                    });
            System.out.println("sent my data to others to match");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // needed to get a list from the events controller
    public void parseEventWrapper(EventWrapper ew){
        Iterable<Event> events = ew.getEvents();
        eventRepository.deleteAll();
        eventRepository.saveAll(events);
        System.out.println("sending list of events");
    }

    //this creates the timestamd which is an atomic integer
    public Event createTimestamp(Event event, int userid){
        int time = timestamp.incrementAndGet();
        event.setTimestamp(time);
        System.out.println("for user: " +userid+ " purchased event with timestamp: "+event.getTimestamp());
        return eventRepository.save(event);
    }

    public void addEventList(Iterable<Event>events){
        eventRepository.saveAll(events);
    }
}

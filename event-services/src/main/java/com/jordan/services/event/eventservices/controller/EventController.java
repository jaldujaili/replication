package com.jordan.services.event.eventservices.controller;

import com.jordan.services.event.eventservices.membership.EventMembershipStates;
import com.jordan.services.event.eventservices.model.EventId;
import com.jordan.services.event.eventservices.model.EventWrapper;
import com.jordan.services.event.eventservices.repositories.EventRepository;
import com.jordan.services.event.eventservices.model.Event;
import com.jordan.services.event.eventservices.model.User;
import com.jordan.services.event.eventservices.services.EventService;
import com.jordan.services.event.eventservices.services.MemberService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jordan on 3/28/18.
 */
@RestController
public class EventController {
    ReentrantLock lock = new ReentrantLock();
//        String UserNode =  "http://localhost:4454/";
    String UserNode =  "http://mc01:2000/";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private EventService eventService;

    @RequestMapping(value="/list", method = RequestMethod.GET)
    public List<Event> findAll(){
        return eventService.getEvents();
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public Event findEvent(@PathVariable("id") int id){
        return eventService.validateEvent(id);
    }

    @RequestMapping(value="/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public EventId createEvent(@RequestBody Event input){
        Event event = null;
        EventId eventId = null;
        try{
            lock.lock();
            if(!stateMachine.getState().getId().equals(EventMembershipStates.LEADER)){ //only for non leaders will execute the if
                event = eventService.createEventWId(input.getEventid(), input.getEventname(), input.getAvail(), input.getUserid());
                eventId = new EventId(event.getEventid());
            }else {//leaders will excute this code
                event = eventService.createEvent(input.getEventname(), input.getNumTickets(), input.getUserid());
                eventId = new EventId(event.getEventid());

                System.out.println("event created id: " + event.getEventid());
            }
        }finally {
            lock.unlock();
            if(stateMachine.getState().getId().equals(EventMembershipStates.LEADER)){//after creating the event will send it out
                memberService.resendToNodes(eventRepository.findByEventid(event.getEventid()),false);
            }
            return eventId;
        }
    }

    @RequestMapping(value="/purchase/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void purchaseTickets(@PathVariable("id") int id, @RequestBody Event input){
        if(stateMachine.getState().getId().equals(EventMembershipStates.FOLLOWER)){
            eventService.addEventToQueue(input); //followers will add its events to a method that determines if it can just be saved or put in queue and wait
        }else {
            Event ev = eventService.purchase(id, input); // purchase() is synchronized
            memberService.resendToNodes(ev, true);
        }
    }



    //updates the all the events in a node
    @RequestMapping(value="/updateEvents", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updatesEvents(@RequestBody EventWrapper input){
        eventService.parseEventWrapper(input);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }
}

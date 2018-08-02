package com.jordan.services.frontend.controller;

import com.jordan.services.frontend.model.*;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by jordan on 3/29/18.
 */
@RestController
public class FrontendController {
//    String UserNode =  "http://localhost:4454/";
    String UserNode =  "http://mc01:2000/";

    @Autowired
    Master masters;

    /*Spring beans are just object instances that are managed by the Spring container*/
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /*annotation for dependency injection. */
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value="/events", method = RequestMethod.GET)
    public List<Event> getAllEvents(){
        try {
            URI eventNode = new URI("http", null, masters.getIp(), masters.getPort(), "/list", null, null);

            ResponseEntity<List<Event>> eventsResponse = this.restTemplate.exchange(
                    eventNode, HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>() {
                    });
            return eventsResponse.getBody();
        }catch (Exception e){

        }
        return null;
    }

    @RequestMapping(value="/events/{id}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable("id") int id){

        try {
            URI eventNode=new URI("http", null, masters.getIp(), masters.getPort(),String.valueOf(id), null, null);

            ResponseEntity<Event> eventResponse = this.restTemplate.exchange(
                    eventNode, HttpMethod.GET,
                    null, new ParameterizedTypeReference<Event>() { //parameterizedtypereferce- this class is to enable capturing and passing a generic Type
                    });
            return  eventResponse.getBody();
        }catch(Exception e){
            throw new NoSuchElementException("");
        }
    }

    @RequestMapping(value="/users/{userid}", method = RequestMethod.GET)
    public UserEvent getUser(@PathVariable("userid") int userid){
        try {
            ResponseEntity<UserEvent> userResponse = this.restTemplate.exchange(
                    UserNode + String.valueOf(userid), HttpMethod.GET,
                    null, new ParameterizedTypeReference<UserEvent>() {
                    });
            UserEvent ue = userResponse.getBody();
            List<Event> events = userResponse.getBody().getTickets(); //from the userResponse body, there is an array of tickets which we put in the events arraylist
            List<Event> fullEvents = new ArrayList<>();
            for(Event event :events) {
                try{
                    URI eventNode=new URI("http", null, masters.getIp(), masters.getPort(),String.valueOf(event.getEventid()), null, null);

                    ResponseEntity<Event> eventResponse = this.restTemplate.exchange( //every event i get i make another call to get the full info
                            eventNode,
                            HttpMethod.GET, null, new ParameterizedTypeReference<Event>() {
                            });

                    fullEvents.add(eventResponse.getBody());
                }catch(Exception e){
//                    e.printStackTrace();
                }
            }

            UserEvent user = new UserEvent(userResponse.getBody().getUserid(), //returns user with fully formed json
                        userResponse.getBody().getUsername(), fullEvents);

            return  user;
        }catch(Exception e){
            throw new NoSuchElementException("");
        }
    }

    /*https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html#postForObject-java.lang.String-java.lang.Object-java.lang.Class-java.lang.Object...-*/
    @RequestMapping(value="/events/create", method = RequestMethod.POST)
    public JSONObject createEvent(@RequestBody Event input){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        validateUser(input.getUserid());
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("userid", String.valueOf(input.getUserid()));
        jsonRequest.put("eventname", input.getEventname());
        jsonRequest.put("numtickets", String.valueOf(input.getNumTickets()));
        JSONObject jsonResponse = new JSONObject();
        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
        try{
            URI eventNode=new URI("http", null, masters.getIp(), masters.getPort(),"/create", null, null);

            ResponseEntity<Event> eventResponse = this.restTemplate.exchange(
                    eventNode, HttpMethod.POST, request,
                    new ParameterizedTypeReference<Event>() {
                    });

            jsonResponse.put("eventid", String.valueOf(eventResponse.getBody().getEventid()));
            return jsonResponse;
        }catch(Exception e){
            throw new NoSuchElementException("");
        }
    }

    @RequestMapping(value="events/{eventid}/purchase/{userid}", method = RequestMethod.POST)
    public void purchaseTickets(@PathVariable("eventid") int eventid,
                                 @PathVariable("userid") int userid,
                                 @RequestBody Event input){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        validateUser(userid);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("userid", String.valueOf(userid));
        jsonRequest.put("eventid", String.valueOf(eventid));
        jsonRequest.put("tickets", String.valueOf(input.getTickets()));

        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
        try{
            URI eventNode=new URI("http", null, masters.getIp(), masters.getPort(),"/purchase/" + String.valueOf(eventid) , null, null);

            ResponseEntity<Event> eventResponse = this.restTemplate.exchange(
                    eventNode,
                    HttpMethod.POST, request, new ParameterizedTypeReference<Event>() {
                    });

        }catch(Exception e){
            throw new NoSuchElementException("couldn't purchase for user: "+userid);
        }
    }

    @RequestMapping(value="/users/create", method = RequestMethod.POST)
    public UserId createUser(@RequestBody User input){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("username", input.getUsername());

        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
        try{
            ResponseEntity<UserId> userResponse = this.restTemplate.exchange(
                    UserNode + "create", HttpMethod.POST, request,
                    new ParameterizedTypeReference<UserId>() {
                    });

            UserId us = new UserId(userResponse.getBody().getUserid());
            return us;
        }catch(Exception e){
            throw new NoSuchElementException("");
        }
    }

    @RequestMapping(value="/users/{userid}/tickets/transfer", method = RequestMethod.POST)
    public void transferTickets(@PathVariable("userid") int userid, @RequestBody Ticket input){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("eventid", String.valueOf(input.getEventid()));
        jsonRequest.put("tickets", String.valueOf(input.getTickets()));
        jsonRequest.put("targetuser", String.valueOf(input.getTargetuser()));

        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
        try{
            ResponseEntity<User> userResponse = this.restTemplate.exchange(
                    UserNode+userid+"/tickets/transfer",
                    HttpMethod.POST, request, new ParameterizedTypeReference<User>() {
                    });

        }catch(Exception e){
            throw new NoSuchElementException("");
        }
    }

    private int validateUser(int userid) throws NoSuchElementException{
        try {
            ResponseEntity<User> userResponse = this.restTemplate.exchange(
                    UserNode + String.valueOf(userid), HttpMethod.GET,
                    null, new ParameterizedTypeReference<User>() {
                    });
            return userid;
        }catch(Exception e){
            throw new NoSuchElementException("user does not exist " + userid);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }

}

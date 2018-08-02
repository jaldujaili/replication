package com.jordan.services.event.eventservices.controller;

import com.jordan.services.event.eventservices.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

/**
 * Created by jordan on 4/14/18.
 */
@RestController
public class HeartbeatController {

    @Autowired
    MemberService memberService;
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value="/heartbeat/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void heartbeat(@PathVariable("id") int id){
        memberService.updateStatus(id); // this updates the status if the node is good
    }

    //returns 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }
}

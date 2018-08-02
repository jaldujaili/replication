package com.jordan.services.event.eventservices.controller;

import com.jordan.services.event.eventservices.model.Member;
import com.jordan.services.event.eventservices.model.MyNode;
import com.jordan.services.event.eventservices.services.EventService;
import com.jordan.services.event.eventservices.services.HeartbeatService;
import com.jordan.services.event.eventservices.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jordan on 4/12/18.
 */
@RestController
public class MembershipController {
    ReentrantLock lock = new ReentrantLock();

    @Autowired
    MemberService memberService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MyNode myNode;

    @Autowired
    EventService eventService;

    @Autowired
    HeartbeatService heartbeatService;


    @RequestMapping(value="/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public List<Member> registerMember(@RequestBody Member input){
        Member member=null;
        try{
            lock.lock();
                //creates the memeber
                member = memberService.createMember(input.getIp(), input.getPort(), true, false, input.isFront());
                try{
                    eventService.sendSnapshot(member); //need this to send to replicate to the member
                }catch(URISyntaxException e){

                }
            return memberService.getMembers();
        }finally {
            lock.unlock();
            try{
                memberService.sendMembershipsToOthers(member); //send a single member to node
            }catch(Exception e){

            }
        }
    }

    @RequestMapping(value="/removeMember/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeMember(@PathVariable("id") int id){
        memberService.removeMember(id); // removes the memeber
        System.out.println("Member " + id+" was removed, new member count: " + memberService.getCount());
    }

    @RequestMapping(value="/createMember", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createMember(@RequestBody Member input){
        try{
            lock.lock();
            memberService.createMember(input.getIp(), input.getPort(), true, false, input.isFront());
        }finally {
            lock.unlock();
        }
    }


    @RequestMapping(value="/election/{id}", method = RequestMethod.GET)
    public JSONObject election(@PathVariable("id") int id){
        int myId = myNode.getNodeid();
        if(myId > id){
            JSONObject jsonResponse = new JSONObject();
            try{
                jsonResponse.put("status", "ok");
            }catch (Exception e){
                e.printStackTrace();
            }
            heartbeatService.startElection();
            return jsonResponse;
        }
        return null;
    }

    @RequestMapping(value="/newLeader/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void newLeader(@PathVariable("id") int id){
        memberService.updateLeader(id);
        System.out.println("new leader is: " + id);
        heartbeatService.restartHeartbeat();
    }

    @RequestMapping(value="/members", method = RequestMethod.GET)
    public List<Member> getMembers(){
        return (List<Member>) memberService.getMembers();
    }

    @RequestMapping(value="/member/{id}", method = RequestMethod.GET)
    public Member findMember(@PathVariable("id") int id){
        return memberService.validateMember(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }

}

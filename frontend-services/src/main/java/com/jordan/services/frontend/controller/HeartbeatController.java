package com.jordan.services.frontend.controller;

import com.jordan.services.frontend.model.Master;
import com.jordan.services.frontend.services.HeartbeatService;
import com.jordan.services.frontend.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jordan on 4/19/18.
 */
@Controller
public class HeartbeatController {

    @Autowired
    MemberService memberService;

    @Autowired
    HeartbeatService heartbeatService;



    @RequestMapping(value="/newLeader/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void newLeader(@PathVariable("id") int id){
        memberService.dethrownFallenLeader();
        memberService.updateLeader(id);
        System.out.println("new leader is: " + id);

        heartbeatService.restartHeartbeat(id);
    }

    @RequestMapping(value="/heartbeat/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void heartbeat(@PathVariable("id") int id){
        memberService.updateStatus(id, true);
    }
}

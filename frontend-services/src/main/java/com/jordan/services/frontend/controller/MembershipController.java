package com.jordan.services.frontend.controller;

import com.jordan.services.frontend.model.Member;
import com.jordan.services.frontend.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jordan on 4/19/18.
 */
@Controller
public class MembershipController {
    ReentrantLock lock = new ReentrantLock();

    @Autowired
    MemberService memberService;

    @RequestMapping(value="/removeMember/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeMember(@PathVariable("id") int id){
        memberService.removeMember(id);
        System.out.println("Member " + id+" was removed, new member count: " + memberService.getCount());
    }

    @RequestMapping(value="/createMember", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createMember(@RequestBody Member input){
        try{
            lock.lock();
            memberService.createMember(input.getMemberId(),input.getIp(), input.getPort(), true, false);
        }finally {
            lock.unlock();
        }
    }
}

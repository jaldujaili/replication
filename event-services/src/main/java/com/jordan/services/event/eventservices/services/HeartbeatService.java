package com.jordan.services.event.eventservices.services;

import com.jordan.services.event.eventservices.membership.EventMembershipEvents;
import com.jordan.services.event.eventservices.membership.EventMembershipStates;
import com.jordan.services.event.eventservices.model.Member;
import com.jordan.services.event.eventservices.model.MyNode;
import com.jordan.services.event.eventservices.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by jordan on 4/14/18.
 */
public class HeartbeatService {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    MyNode myNode;

    @Autowired
    EventService eventService;

    //heartbeat for follower nodes
    public void heartbeat(String uri){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                System.out.println("Leader is active");
                while (running){
                    try{
                        ResponseEntity<Member> memberResponse = restTemplate.exchange(
                                uri, HttpMethod.GET, null,
                                new ParameterizedTypeReference<Member>() {
                                });
                        Thread.sleep(3000);
                    }catch(InterruptedException e){

                    }catch (Exception e){
                        System.out.println("couldn't reach Leader will initiate election");
                        stateMachine.sendEvent(EventMembershipEvents.TIMEOUT);
                        startElection();
                        running = false;
                    }
                }
            }
        });
    }

    // the leaders heartbeat
    public void leaderHeartbeat(){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                int count = 0;
                while(running) {
                    List<Member> members = (List<Member>)memberRepository.findAll();
                    for(Member member : members){
                        if(member.isLeader()){
                            continue;
                        }
                        URI uri= null;
                        try{
                            uri = new URI("http", null, member.getIp(), member.getPort(),"/heartbeat/"+myNode.getNodeid(), null, null);
                        }catch(URISyntaxException e){
                            e.printStackTrace();
                        }
                        try{
                            ResponseEntity<Member> memberResponse = restTemplate.exchange(
                                    uri, HttpMethod.GET, null,
                                    new ParameterizedTypeReference<Member>() {
                                    });
                        }catch (Exception e) {
                            if (stateMachine.getState().getId().equals(EventMembershipStates.LEADER)) {
                                System.out.println("====deleting member with id: " + member.getMemberId());
                                int fallenId=member.getMemberId();

                                memberRepository.delete(member);
                                removeMemberFromOtherNodes(fallenId);
                            }
                        }
                    }
                    try{
                        if(count == 0 || count != memberRepository.count()){
                            count = (int) memberRepository.count();
                        }
                        Thread.sleep(3000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //when a member fails, will remove it from memory
    public void removeMemberFromOtherNodes(int fallenMember){
        List<Member> members = (List<Member>) memberRepository.findAll();
        for(Member member: members){
            if(member.getMemberId() != myNode.getNodeid()) {
                URI uri= null;
                try{
                    uri = new URI("http", null, member.getIp(), member.getPort(),"/removeMember/" + fallenMember, null, null);
                }catch(URISyntaxException e){
                    e.printStackTrace();
                }
                try {
                    ResponseEntity<Member> memberResponse = restTemplate.exchange(
                            uri, HttpMethod.POST, null,
                            new ParameterizedTypeReference<Member>() {
                            });
                } catch (Exception e) {
                    System.out.println("couldnt delete from member: " + member.getMemberId());
                }
            }
        }
    }

    //this initiates an election by removing the leader
    public void startElection(){
        Member fallenLeader = memberRepository.findByIsLeader(true);
        if(fallenLeader !=null) {
            memberRepository.delete(fallenLeader);
        }

        List<Member> members = (List<Member>)memberRepository.findAll();
        int count = (int) memberRepository.count() -1; //we have this so we can do a countdown, if 0 we elect
        for(Member member : members) {
            int id = member.getMemberId();

            //making sure we dont include the frontend nodes as a voter
            if(member.isFront()){
                count = count -1;
            }

            if(id != myNode.getNodeid() && !member.isFront()) {
                if (id < myNode.getNodeid()) {
                    URI uri= null;
                    try{
                        uri = new URI("http", null, member.getIp(), member.getPort(),"/election/" + myNode.getNodeid(), null, null);
                    }catch(URISyntaxException e){
                        e.printStackTrace();
                    }
                    try {
                        ResponseEntity<Member> memberResponse = restTemplate.exchange(
                                uri, HttpMethod.GET, null,
                                new ParameterizedTypeReference<Member>() {
                                });
                        if(memberResponse.getBody()==null) {
                            count = count - 1;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }

        if (count <= 0){
            //noreply from all avail nodes means it will become leader
            stateMachine.sendEvent(EventMembershipEvents.NOREPLY);
            transitionLeadership();
            leaderHeartbeat();
        }
        //response turns a nominated to follower
        stateMachine.sendEvent(EventMembershipEvents.RESPONSE);
    }

    //once a node become nominated to leader, needs to tell others to update who the leader is
    public void transitionLeadership(){
        List<Member> members = (List<Member>) memberRepository.findAll();
        for(Member member : members){
            if(!member.isLeader()) {
                URI uri = null;
                try {
                    uri = new URI("http", null, member.getIp(), member.getPort(), "/newLeader/" + myNode.getNodeid(), null, null);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    ResponseEntity<Member> memberResponse = restTemplate.exchange(
                            uri, HttpMethod.POST, null,
                            new ParameterizedTypeReference<Member>() {
                            });
                    if(!member.isFront()) {
                        eventService.sendSnapshot(member);
                    }
                } catch (Exception e) {
                    System.out.println("elected void");
                }
            }
        }
    }

    //restarts the heartbeat to the new leader
    public void restartHeartbeat(){
        Member newLeader = memberRepository.findByIsLeader(true); //find the node that is leader
        String ip = newLeader.getIp();
        int port = newLeader.getPort();
        URI uri= null;
        try{
            uri = new URI("http", null, ip, port,"/heartbeat/"+ myNode.getNodeid(), null, null);
        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        heartbeat(uri.toString());
    }
}

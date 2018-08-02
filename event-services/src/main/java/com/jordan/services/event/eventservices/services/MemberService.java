package com.jordan.services.event.eventservices.services;


import com.google.gson.Gson;
import com.jordan.services.event.eventservices.model.Event;
import com.jordan.services.event.eventservices.model.Member;
import com.jordan.services.event.eventservices.model.MyNode;
import com.jordan.services.event.eventservices.repositories.MemberRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Created by jordan on 4/13/18.
 */
@Service
public class MemberService {
    private final static Logger log = Logger.getLogger(MemberService.class .getName());

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyNode myNode;

    @Autowired
    private HeartbeatService heartbeatService;

    @Autowired
    private StateMachine stateMachine;

    //from the membership controller
    //saves the member to memory
    public Member createMember(String ip, Integer port, boolean goodStatus, boolean isLeader, boolean isFront){
        if(!memberRepository.existsByIpAndPort(ip, port)){
            Member member = new Member(ip,port,goodStatus, isLeader, isFront);
            if(isFront){
                member.setIsFront(true);
            }
            memberRepository.save(member);
            System.out.println("member count: " + memberRepository.count());
            return member;
        }
        return null;
    }

    //from the membership controller
    // when a new node registers, you need to send membership data to all other nodes.
    public void sendMembershipsToOthers(Member member) throws URISyntaxException {
        List<Member> members = (List<Member>) memberRepository.findAll();
        log.info("sending newly registerd node to the rest of the nodes. --> numbers of members should increase");
        for(Member m : members) {
            if (m.getMemberId() != myNode.getNodeid() && m.getMemberId()!=Integer.valueOf(member.getMemberId())) {
                URI uri=new URI("http", null, m.getIp(), m.getPort(),"/createMember", null, null);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                try {
                    Gson gson = new Gson();
                    String jsonMember = gson.toJson(member);

                    HttpEntity<String> request = new HttpEntity<String>(jsonMember, headers);

                    ResponseEntity<Member> memberResponse = restTemplate.exchange(
                            uri, HttpMethod.POST, request,
                            new ParameterizedTypeReference<Member>() {
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeMember(int id){
        Member m = memberRepository.findByMemberId(id);
        memberRepository.delete(m);
    }

    //every heart beat will set the status to good.
    public void updateStatus(int id){
        Member member=memberRepository.findByMemberId(id);
        try{
            member.setGoodStatus(true);
            memberRepository.save(member);
        }catch(NullPointerException e){

        }

    }

    //updates a follower to a leader
    public void updateLeader(int id){
        Member member = memberRepository.findByMemberId(id);
        try{
            member.setLeader(true);
            memberRepository.save(member);
        }catch(NullPointerException e){
            System.out.println("could not update the leader");
        }
    }

    public List<Member> getMembers() {
        return (List<Member>)memberRepository.findAll();
    }

    public void addMember(Member member){
        memberRepository.save(member);
    }

    public Member validateMember(int id) throws NoSuchElementException {
        Member member = memberRepository.findByMemberId(id);
        if(member == null){
            throw new NoSuchElementException("event does not exist" + id);
        }
        return member;
    }

    //from the event controller
    //this iterates all the members and sends updated info
    public void resendToNodes(Event event, boolean purchase) {
        Iterable members = memberRepository.findAll();
        Iterator<Member> iter = members.iterator();
        while (iter.hasNext()) {
            Member member = iter.next();
            if(!member.isLeader() && !member.isFront()){
                try {
                    sendEventToNode(event, member, purchase);
                }catch(Exception e){
                    System.out.println(event.getEventname()+ " "+ member.getPort());

                }
            }
        }
    }

    //from resendToNode()^^ sends info to this method to determine if purchase/event create and makes a request to the event controller
    public void sendEventToNode(Event event, Member member, boolean purchase){
        URI uri= null;
        try{
            if(purchase){
                uri = new URI("http", null, member.getIp(), member.getPort(),"/purchase/"+ event.getEventid(), null, null);
            }else {
                uri = new URI("http", null, member.getIp(), member.getPort(),"/create", null, null);
            }

        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonRequest = new JSONObject();
        boolean retry = true;
        while(retry) {
            try {
                jsonRequest.put("userid", String.valueOf(event.getUserid()));
                jsonRequest.put("eventid", String.valueOf(event.getEventid()));
                jsonRequest.put("avail", String.valueOf(event.getAvail()));
                jsonRequest.put("purchased", String.valueOf(event.getPurchased()));
                jsonRequest.put("eventname", event.getEventname());
                if(purchase) {
                    jsonRequest.put("timestamp", String.valueOf(event.getTimestamp()));
                }
                HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
                ResponseEntity<Member> memberResponse = restTemplate.exchange(
                        uri, HttpMethod.POST, request,
                        new ParameterizedTypeReference<Member>() {
                        });
                if(purchase){
                    System.out.println("copying leader of purchase to follower for event: "+ event.getEventid());
                }else{
                    System.out.println("follower node: "+member.getMemberId()+" made a created copy of: "+ event.getEventid());
                }
                retry = false;
            } catch (Exception e) {

                try{
                    Thread.sleep(2000);
                }catch (InterruptedException i){

                }
            }
        }
    }

    public long getCount(){
        return memberRepository.count();
    }
}

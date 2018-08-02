package com.jordan.services.event.eventservices;

import com.jordan.services.event.eventservices.membership.EventMembershipEvents;
import com.jordan.services.event.eventservices.membership.EventMembershipStates;
import com.jordan.services.event.eventservices.model.Event;
import com.jordan.services.event.eventservices.model.Member;
import com.jordan.services.event.eventservices.model.MyNode;
import com.jordan.services.event.eventservices.services.EventService;
import com.jordan.services.event.eventservices.services.HeartbeatService;
import com.jordan.services.event.eventservices.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.util.List;


@SpringBootApplication
public class EventServicesApplication implements ApplicationRunner {
	@Autowired
	private EventService eventService;
	@Autowired
	private MemberService memberService;

	@Autowired
	private StateMachine<EventMembershipStates, EventMembershipEvents> stateMachine;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	public HeartbeatService heartbeatService() {
		return new HeartbeatService();
	}

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HeartbeatService heartbeatService;

	private boolean isLeader;
	private int port;
	private String ip;
	private int nodeid;

	@Bean
	public MyNode myNode(){
		return new MyNode(isLeader, port, ip, nodeid);
	}

	@Bean
	public EventQueue eventQueue(){
		return new EventQueue();
	}
	public static void main(String[] args){
		SpringApplication.run(EventServicesApplication.class, args);
	}

	//setting up the node
	@Override
	public void run(ApplicationArguments args) throws Exception {
		eventService.createEvent("Harry Potter Muscial", 100, 20);
		eventService.createEvent("49er Parade", 100, 14);
		eventService.createEvent("Halloween Party", 100, 44);
		eventService.createEvent("Back to the Future", 100, 15);
		stateMachine.start();

		isLeader = args.getOptionValues("type").get(0).equals("leader") ? true : false;
		myNode().setLeader(isLeader);

		port = Integer.parseInt(args.getOptionValues("server.port").get(0));
		myNode().setPort(port);
		ip = Inet4Address.getLocalHost().getHostAddress();
		myNode().setIp(ip);
		//if leader it will create a heart beat, if not it will register with leader and start a heart beat
		if (isLeader){
			stateMachine.sendEvent(EventMembershipEvents.FIRSTNODE);
			Member m = memberService.createMember(ip, port, true, true,false);
			myNode().setNodeid(m.getMemberId());
			nodeid = m.getMemberId();
			heartbeatService.leaderHeartbeat();
		}else{
			String masterIp = args.getOptionValues("masterIp").get(0);
			int masterPort = Integer.parseInt(args.getOptionValues("masterPort").get(0));
			registerWithLeader(ip, port, masterIp, masterPort);
			initiateHeartbeat(masterIp, masterPort);
			eventQueue().run();
		}
	}

	public void registerWithLeader(String ip, int port, String masterIp, int masterPort){
		getMemberships(ip, port, masterIp, masterPort);
		getUpdatedData( masterIp, masterPort);
	}

	//will need to get updated data from leader when starting up node
	public void getUpdatedData(String masterIp, int masterPort){
		final String uri = masterIp +":" +masterPort +"/list";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try{
			ResponseEntity<List<Event>> eventsResponse = this.restTemplate.exchange(
					uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Event>>() {
					});
			Iterable<Event>events = eventsResponse.getBody();
			eventService.addEventList(events);

		}catch(Exception e ){
			e.printStackTrace();
		}
	}

	//will need memberships from other nodes when starting up
	public void getMemberships(String ip, int port, String masterIp, int masterPort){
		final String uri = masterIp +":" +masterPort +"/register";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonRequest = new JSONObject();
		try{
			jsonRequest.put("ip", ip);
			jsonRequest.put("port", port);
			HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toString(), headers);
			System.out.println("registering with leader");
			ResponseEntity<List<Member>> memberResponse = this.restTemplate.exchange(
					uri, HttpMethod.POST, request,
					new ParameterizedTypeReference<List<Member>>() {
					});
			List<Member>members = memberResponse.getBody();
			for(Member member: members){
				memberService.addMember(member);
			}
			nodeid = members.get(members.size()-1).getMemberId();
			myNode().setNodeid(nodeid);
			System.out.println("My id: " + nodeid);
			System.out.println("Current member count after adding myself: "+ memberService.getCount());

		}catch(Exception e){
			System.out.println("could not register with leader");
		}
	}

	//creates a heart beat if follower node
	public void initiateHeartbeat(String masterIp, int masterPort){
		final String uri = masterIp +":" +masterPort +"/heartbeat/"+nodeid;
		heartbeatService.heartbeat(uri.toString());
	}

}

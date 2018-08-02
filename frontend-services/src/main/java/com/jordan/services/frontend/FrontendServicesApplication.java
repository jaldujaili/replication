package com.jordan.services.frontend;


import com.jordan.services.frontend.model.Master;
import com.jordan.services.frontend.model.Member;
import com.jordan.services.frontend.model.MyNode;
import com.jordan.services.frontend.services.HeartbeatService;
import com.jordan.services.frontend.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootApplication
public class FrontendServicesApplication implements ApplicationRunner {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyNode myNode;

	@Autowired
	private MemberService memberService;

	@Bean
	public HeartbeatService heartbeatService() {
		return new HeartbeatService();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private HeartbeatService heartbeatService;

	private int port;
	private String ip;
	private int nodeid;
	private String masterIp;
	private int masterPort;

	@Bean
	public MyNode myNode(){
		return new MyNode(port, ip, nodeid);
	}

	@Bean
	public Master master(){
		return new Master();
	}

	public static void main(String[] args) {
		SpringApplication.run(FrontendServicesApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		port = Integer.parseInt(args.getOptionValues("server.port").get(0));
		myNode().setPort(port);

		ip = Inet4Address.getLocalHost().getHostAddress();
		myNode().setIp(ip);

		masterIp = args.getOptionValues("masterIp").get(0);
		master().setIp(masterIp);
		masterPort = Integer.parseInt(args.getOptionValues("masterPort").get(0));
		master().setPort(masterPort);
		registerWithLeader(ip, port, true, masterIp, masterPort);
		initiateHeartbeat(masterIp, masterPort);
	}

	public void registerWithLeader(String ip, int port, boolean status, String masterIp, int masterPort){
//		final String uri = masterIp +":" +masterPort +"/register";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonRequest = new JSONObject();

		try{
			URI uri = new URI("http", null, masterIp, masterPort, "/register", null, null);
			jsonRequest.put("ip", ip);
			jsonRequest.put("port", port);
			jsonRequest.put("isFront", String.valueOf(true));
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
			e.printStackTrace();
//			System.out.println("could not register with leader");
		}
	}
	public void initiateHeartbeat(String masterIp, int masterPort){
		try{
			URI uri = new URI("http", null, masterIp, masterPort, "/heartbeat/"+nodeid, null, null);
			heartbeatService.heartbeat(uri.toString());

		}catch (URISyntaxException e){

		}
	}

}

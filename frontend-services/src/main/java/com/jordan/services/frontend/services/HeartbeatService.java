package com.jordan.services.frontend.services;

import com.jordan.services.frontend.model.Member;
import com.jordan.services.frontend.model.MyNode;
import com.jordan.services.frontend.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by jordan on 4/18/18.
 */
@Service
public class HeartbeatService {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    MyNode myNode;

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
                        Thread.sleep(5000);
                    }catch(InterruptedException e){

                    }catch (Exception e){
//                        e.printStackTrace();
                        running = false;
                    }
                }
            }
        });
    }


    public void restartHeartbeat(int id){
        Member newLeader = memberRepository.findByMemberId(id);
        String ip = newLeader.getIp();
        int port = newLeader.getPort();
        URI uri= null;
        try{
            uri = new URI("http", null, ip, port,"/heartbeat/"+myNode.getNodeid(), null, null);
        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        heartbeat(uri.toString());
    }
}

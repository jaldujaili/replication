package com.jordan.services.frontend.services;


import com.jordan.services.frontend.model.Master;
import com.jordan.services.frontend.model.Member;
import com.jordan.services.frontend.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Created by jordan on 4/18/18.
 */
@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    Master master;

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

    public void updateLeader(int id){
        Member member = memberRepository.findByMemberId(id);
        try{
            member.setLeader(true);
            memberRepository.save(member);
            master.setIp(member.getIp());
            master.setPort(member.getPort());
        }catch(NullPointerException e){
            System.out.println("could not update the leader");
        }
    }

    public void dethrownFallenLeader(){
        Member m= memberRepository.findByIsLeader(true);
        m.setLeader(false);
        memberRepository.save(m);
    }
    public Member createMember(Integer id, String ip, Integer port, boolean goodStatus, boolean isLeader){
        if(!memberRepository.existsByIpAndPort(ip, port)){
            Member member = new Member(ip,port,goodStatus, isLeader);
            member.setMemberId(id);
            memberRepository.save(member);
            return member;
        }
        return null;
    }

    public void updateStatus(int id, boolean goodStatus){
        Member member=memberRepository.findByMemberId(id);
        try{
            member.setGoodStatus(true);
            memberRepository.save(member);
        }catch(NullPointerException e){

        }

    }
    public void removeMember(int id){
        Member m = memberRepository.findByMemberId(id);
        memberRepository.delete(m);
    }

    public long getCount(){
        return memberRepository.count();
    }
}

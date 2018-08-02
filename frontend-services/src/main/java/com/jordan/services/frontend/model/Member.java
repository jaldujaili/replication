package com.jordan.services.frontend.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by jordan on 4/12/18.
 */
@Entity
public class Member {
    @Id
//    @GeneratedValue
    private Integer memberId;
    @Column
    private String ip;
    @Column
    private Integer port;
    @Column
    private boolean goodStatus;
    @Column
    private boolean isLeader;
    @Column
    private boolean isFront;

    public Member(String ip, Integer port, boolean goodStatus, boolean isLeader) {
        this.ip = ip;
        this.port = port;
        this.goodStatus = goodStatus;
        this.isLeader = isLeader;
    }
    public Member(String ip, Integer port, boolean goodStatus, boolean isLeader, boolean isFront) {
        this.ip = ip;
        this.port = port;
        this.goodStatus = goodStatus;
        this.isLeader = isLeader;
        this.isFront = isFront;
    }

    protected Member() {

    }

    public boolean isFront() {
        return isFront;
    }

    public void setIsFront(boolean front) {
        isFront = front;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public boolean isGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(boolean goodStatus) {
        this.goodStatus = goodStatus;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package com.jordan.services.event.eventservices.model;

/**
 * Created by jordan on 4/16/18.
 */
public class MyNode {
    private boolean isLeader;
    private int port;
    private String ip;
    private int nodeid;

    public MyNode(boolean isLeader, int port, String ip, int nodeid) {
        this.isLeader = isLeader;
        this.port = port;
        this.ip = ip;
        this.nodeid = nodeid;
    }
    public MyNode(){

    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getNodeid() {
        return nodeid;
    }

    public void setNodeid(int nodeid) {
        this.nodeid = nodeid;
    }
}

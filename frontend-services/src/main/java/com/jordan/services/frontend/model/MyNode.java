package com.jordan.services.frontend.model;

/**
 * Created by jordan on 4/18/18.
 */
public class MyNode {
    private int port;
    private String ip;
    private int nodeid;

    public MyNode(int port, String ip, int nodeid) {
        this.port = port;
        this.ip = ip;
        this.nodeid = nodeid;
    }
    public MyNode(){

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


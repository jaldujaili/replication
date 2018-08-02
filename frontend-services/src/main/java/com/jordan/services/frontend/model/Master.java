package com.jordan.services.frontend.model;

/**
 * Created by jordan on 4/20/18.
 */
public class Master {
    private int port;
    private String ip;

    public Master(int port, String ip){
        this.port = port;
        this.ip = ip;
    }
    public Master(){

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
}

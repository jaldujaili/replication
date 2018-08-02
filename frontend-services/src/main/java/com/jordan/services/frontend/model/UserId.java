package com.jordan.services.frontend.model;

/**
 * Created by jordan on 4/4/18.
 */
public class UserId {
    private Integer userid;

    public UserId(Integer userid) {
        this.userid = userid;
    }
    protected UserId(){

    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}

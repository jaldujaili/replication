package com.jordan.services.event.eventservices.membership;

/**
 * Created by jordan on 4/11/18.
 */

//This will help my statemachine config see the events that would be used to transition states
public enum EventMembershipEvents {
    TIMEOUT, RESPONSE, NOREPLY, FIRSTNODE
}

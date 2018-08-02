package com.jordan.services.event.eventservices.config;

import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

/**
 * Created by jordan on 4/11/18.
 */

public class StateMachineListener extends StateMachineListenerAdapter {
    //https://docs.spring.io/autorepo/docs/spring-statemachine/1.2.x-SNAPSHOT/reference/html/sm-listeners.html
    @Override
    public void stateChanged(State from, State to) {
        System.out.println((String.format("Transitioned from %s to %s%n", from == null ?
                "none" : from.getId(), to.getId())));
    }
}
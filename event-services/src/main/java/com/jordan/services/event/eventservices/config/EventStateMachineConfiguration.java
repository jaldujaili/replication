package com.jordan.services.event.eventservices.config;

import com.jordan.services.event.eventservices.membership.EventMembershipEvents;
import com.jordan.services.event.eventservices.membership.EventMembershipStates;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * Created by jordan on 4/11/18.
 */
@Configuration
@EnableStateMachine
public class EventStateMachineConfiguration extends StateMachineConfigurerAdapter<EventMembershipStates, EventMembershipEvents>{
    /*This is a a configuration class that allows me to define what the node states, whether its a follower, nominated or leader.
    * this helps with the transitions*/
    @Override
    public void configure(StateMachineConfigurationConfigurer<EventMembershipStates, EventMembershipEvents> config)
            throws Exception{
        config.withConfiguration().autoStartup(true).listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<EventMembershipStates, EventMembershipEvents> states)
            throws Exception{
        states.withStates()
                .initial(EventMembershipStates.FOLLOWER)
                .state(EventMembershipStates.NOMINATED)
                .end(EventMembershipStates.FOLLOWER)
                .end(EventMembershipStates.LEADER);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EventMembershipStates, EventMembershipEvents> transitions)
            throws Exception{
        transitions.withExternal()
                .source(EventMembershipStates.FOLLOWER).target(EventMembershipStates.LEADER)
                    .event(EventMembershipEvents.FIRSTNODE).and().withExternal()
                .source(EventMembershipStates.FOLLOWER).target(EventMembershipStates.NOMINATED)
                    .event(EventMembershipEvents.TIMEOUT).and().withExternal()
                .source(EventMembershipStates.NOMINATED).target(EventMembershipStates.FOLLOWER)
                    .event(EventMembershipEvents.RESPONSE).and().withExternal()
                .source(EventMembershipStates.NOMINATED).target(EventMembershipStates.LEADER)
                    .event(EventMembershipEvents.NOREPLY).and().withExternal();

    }
}

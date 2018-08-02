package com.jordan.services.event.eventservices.repositories;

import com.jordan.services.event.eventservices.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jordan on 3/16/18.
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Event findByEventid(Integer eventid);
    boolean existsByEventname(String eventname);
    boolean existsByTimestamp(Integer timestamp);
}

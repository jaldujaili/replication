package com.jordan.services.event.eventservices.repositories;

import com.jordan.services.event.eventservices.model.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jordan on 4/13/18.
 */
@Repository
public interface MemberRepository extends CrudRepository<Member, Integer> {
    Member findByMemberId(Integer memberId);
    boolean existsByIpAndPort(String ip, Integer port);
    Member findByIsLeader(boolean isLeader);
}

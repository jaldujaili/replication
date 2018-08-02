package com.jordan.services.frontend.repositories;

import com.jordan.services.frontend.model.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jordan on 4/18/18.
 */
@Repository
public interface MemberRepository extends CrudRepository<Member, Integer> {
    Member findByMemberId(Integer memberId);
    boolean existsByIpAndPort(String ip, Integer port);
    Member findByIsLeader(boolean isLeader);
}

package br.com.nemi.repository;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.membership.Membership;
import br.com.nemi.domain.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByGroup(Group group);

    List<Membership> findByParticipant(Participant participant);

    Optional<Membership> findByParticipantAndGroup(Participant participant, Group group);
}

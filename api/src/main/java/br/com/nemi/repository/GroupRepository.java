package br.com.nemi.repository;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, String> {

    List<Group> findByOwner(Participant owner);
}

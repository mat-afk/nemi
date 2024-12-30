package br.com.nemi.repository;

import br.com.nemi.domain.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, String> {

    Optional<Participant> findByEmail(String email);

    Optional<Participant> findByPhoneNumber(String phoneNumber);
}

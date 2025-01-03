package br.com.nemi.service;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Participant> getParticipants() {
        return this.participantRepository.findAll();
    }

    public Participant getParticipant(String id) {
        return this.participantRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Participant not found with id: " + id)
        );
    }

}

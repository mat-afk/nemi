package br.com.nemi.service;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.CreateParticipantRequestDTO;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.TokenGenerator;
import br.com.nemi.util.exception.BadRequestException;
import br.com.nemi.util.exception.ConflictException;
import br.com.nemi.util.exception.NotFoundException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Participant createParticipant(CreateParticipantRequestDTO request) {

        boolean isEmailValid = EmailValidator.getInstance().isValid(request.email());
        if (!isEmailValid) throw new BadRequestException("Invalid e-mail address");

        Optional<Participant> existingUser = this.participantRepository.findByEmail(request.email());
        if (existingUser.isPresent()) throw new ConflictException("E-mail already in use");

        existingUser = this.participantRepository.findByPhoneNumber(request.phoneNumber());
        if (existingUser.isPresent()) throw new ConflictException("Phone number already in use");

        Participant participant = new Participant();

        participant.setId(TokenGenerator.generateCUID());
        participant.setName(request.name());
        participant.setEmail(request.email());
        participant.setPhoneNumber(request.phoneNumber());
        participant.setCreatedAt(LocalDateTime.now());
        participant.setUpdatedAt(LocalDateTime.now());

        this.participantRepository.save(participant);

        return participant;
    }
}

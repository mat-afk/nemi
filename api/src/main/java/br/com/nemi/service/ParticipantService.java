package br.com.nemi.service;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.UpdateParticipantRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.exception.ConflictException;
import br.com.nemi.exception.ForbiddenException;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.MembershipRepository;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    public List<Participant> getParticipants() {
        return this.participantRepository.findAll();
    }

    public Participant getParticipant(String id) {
        return this.participantRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Participant not found with id: " + id)
        );
    }

    public Participant updateParticipant(String id, UpdateParticipantRequestDTO request) {
        Participant participant = this.participantRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Participant not found with id: " + id));

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!id.equals(authParticipant.getId()))
            throw new ForbiddenException("You don't have permission to update this user");

        Optional<Participant> existingParticipant = this.participantRepository.findByEmail(request.email());

        if (existingParticipant.isPresent() && !existingParticipant.get().getId().equals(id))
            throw new ConflictException("Unavailable e-mail");

        existingParticipant = this.participantRepository.findByPhoneNumber(request.phoneNumber());
        if (existingParticipant.isPresent() && !existingParticipant.get().getId().equals(id))
            throw new ConflictException("Unavailable phone number");

        participant.setEmail(request.email());
        participant.setPhoneNumber(request.phoneNumber());

        this.participantRepository.save(participant);

        return participant;
    }

    public void deleteParticipant(String id) {
        Participant participant = this.participantRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Participant not found with id: " + id));

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!id.equals(authParticipant.getId()))
            throw new ForbiddenException("You don't have permission to delete this user");

        List<Group> groups = this.groupRepository.findByOwner(participant);
        if (!groups.isEmpty()) throw new BadRequestException("This participant owns groups. Delete them first");

        this.membershipRepository.deleteAll(
                this.membershipRepository.findByParticipant(participant)
        );

        this.participantRepository.delete(participant);
    }

}

package br.com.nemi.service;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public Group createGroup(CreateGroupRequestDTO request) {
        Participant owner = this.participantRepository.findById(request.ownerId()).orElseThrow(
                () -> new NotFoundException("Owner not found")
        );

        Group group = new Group();

        group.setId(TokenGenerator.generateCUID());
        group.setName(request.name());
        group.setOwner(owner);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());

        return group;
    }
}

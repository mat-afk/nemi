package br.com.nemi.service;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.group.dto.GroupDetailsResponseDTO;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.CreateParticipantRequestDTO;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public List<GroupDetailsResponseDTO> getGroups() {
        List<Group> groups = this.groupRepository.findAll();

        List<GroupDetailsResponseDTO> response = new ArrayList<>();

        groups.forEach(group -> response.add(new GroupDetailsResponseDTO(group)));

        return response;
    }

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

        group.getParticipants().add(owner);

        this.groupRepository.save(group);

        return group;
    }

    public GroupDetailsResponseDTO addParticipants(
            String groupId,
            List<CreateParticipantRequestDTO> request
    ) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        List<String> participantIdsInGroup = group.getParticipants().stream().map(
                Participant::getId
        ).toList();

        LocalDateTime now = LocalDateTime.now();

        request.forEach(participant -> {
            Optional<Participant> existingParticipant = this.participantRepository.findByEmail(participant.email());

            if (existingParticipant.isEmpty()) {
                Participant newParticipant = new Participant();

                newParticipant.setId(TokenGenerator.generateCUID());
                newParticipant.setName(participant.name());
                newParticipant.setEmail(participant.email());
                newParticipant.setPhoneNumber(participant.phoneNumber());
                newParticipant.setCreatedAt(now);
                newParticipant.setUpdatedAt(now);

                this.participantRepository.save(newParticipant);

                group.getParticipants().add(newParticipant);
            } else {
                boolean participantAlreadyInGroup = participantIdsInGroup.contains(
                        existingParticipant.get().getId()
                );

                if (!participantAlreadyInGroup) {
                    group.getParticipants().add(existingParticipant.get());
                }
            }
        });

        this.groupRepository.save(group);

        return new GroupDetailsResponseDTO(group);
    }
}

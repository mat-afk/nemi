package br.com.nemi.service;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.group.dto.GroupDetailsResponseDTO;
import br.com.nemi.domain.membership.Membership;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.CreateParticipantRequestDTO;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.MembershipRepository;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public List<GroupDetailsResponseDTO> getGroups() {
        List<Group> groups = this.groupRepository.findAll();

        List<GroupDetailsResponseDTO> response = new ArrayList<>();

        groups.forEach(group -> {
            List<Participant> participants =
                    this.membershipRepository.findByGroup(group).stream().map(
                            Membership::getParticipant
                    ).toList();

            response.add(
                new GroupDetailsResponseDTO(
                    group,
                    participants
                )
            );
        });

        return response;
    }

    public GroupDetailsResponseDTO createGroup(CreateGroupRequestDTO request) {
        Participant owner = this.participantRepository.findById(request.ownerId()).orElseThrow(
                () -> new NotFoundException("Owner not found")
        );

        LocalDateTime now = LocalDateTime.now();

        Group group = new Group();
        group.setId(TokenGenerator.generateCUID());
        group.setName(request.name());
        group.setOwner(owner);
        group.setCreatedAt(now);
        group.setUpdatedAt(now);

        this.groupRepository.save(group);

        Membership membership = new Membership();
        membership.setNickname("");
        membership.setParticipant(owner);
        membership.setGroup(group);
        membership.setSince(now);

        this.membershipRepository.save(membership);

        return new GroupDetailsResponseDTO(
                group,
                this.membershipRepository.findByGroup(group).stream().map(
                        Membership::getParticipant
                ).toList()
        );
    }

    public GroupDetailsResponseDTO addParticipants(
            String groupId,
            List<CreateParticipantRequestDTO> request
    ) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        LocalDateTime now = LocalDateTime.now();

        request.forEach(participant -> {
            Optional<Participant> existingParticipant =
                    this.participantRepository.findByEmail(participant.email());

            if (existingParticipant.isEmpty()) {
                Participant newParticipant = new Participant();

                String email = participant.email() == null
                        ? null
                        : participant.email().isBlank() ? null : participant.email();

                String phoneNumber = participant.phoneNumber() == null
                        ? null
                        : participant.phoneNumber().isBlank() ? null : participant.phoneNumber();

                newParticipant.setId(TokenGenerator.generateCUID());
                newParticipant.setEmail(email);
                newParticipant.setPhoneNumber(phoneNumber);
                newParticipant.setCreatedAt(now);
                newParticipant.setUpdatedAt(now);

                this.participantRepository.save(newParticipant);

                Membership membership = new Membership();
                membership.setNickname(participant.nickname());
                membership.setParticipant(newParticipant);
                membership.setGroup(group);
                membership.setSince(now);

                this.membershipRepository.save(membership);

            } else {
                boolean participantAlreadyInGroup =
                        this.membershipRepository.findByParticipantAndGroup(
                                existingParticipant.get(),
                                group
                        ).isPresent();

                if (!participantAlreadyInGroup) {
                    Membership membership = new Membership();
                    membership.setNickname(participant.nickname());
                    membership.setParticipant(existingParticipant.get());
                    membership.setGroup(group);
                    membership.setSince(now);

                    this.membershipRepository.save(membership);
                }
            }
        });

        return new GroupDetailsResponseDTO(
                group,
                this.membershipRepository.findByGroup(group).stream().map(
                        Membership::getParticipant
                ).toList()
        );
    }
}

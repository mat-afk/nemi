package br.com.nemi.service;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.group.dto.GroupDetailsDTO;
import br.com.nemi.domain.membership.Membership;
import br.com.nemi.domain.participant.AccessType;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.group.dto.AddParticipantInGroupRequestDTO;
import br.com.nemi.domain.participant.dto.ParticipantMembershipDetailsDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.exception.ConflictException;
import br.com.nemi.exception.ForbiddenException;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.MembershipRepository;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.FieldValidator;
import br.com.nemi.util.IdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<Group> getGroups() {
        return this.groupRepository.findAll();
    }

    public Group getGroup(String id) {
        return this.groupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + id));
    }

    public GroupDetailsDTO getGroupDetails(String id) {
        Group group = this.groupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + id));

        List<ParticipantMembershipDetailsDTO> participants = this.membershipRepository
                .findByGroup(group).stream()
                .map(membership ->
                        new ParticipantMembershipDetailsDTO(membership.getParticipant(), membership.getNickname()))
                .toList();

        return new GroupDetailsDTO(group, participants);
    }

    public Group createGroup(CreateGroupRequestDTO request) {
        Participant owner = (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDateTime now = LocalDateTime.now();

        Group group = new Group();
        group.setId(IdProvider.generateCUID());
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

        return group;
    }

    public Group updateGroup(String id, CreateGroupRequestDTO request) {
        Group group = this.groupRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + id)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!group.getOwner().getId().equals(authParticipant.getId()))
            throw new BadRequestException("You don't have permission to update this group");

        group.setName(request.name());
        this.groupRepository.save(group);

        return group;
    }

    public void deleteGroup(String id) {
        Group group = this.groupRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + id)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!group.getOwner().getId().equals(authParticipant.getId()))
            throw new BadRequestException("You don't have permission to delete this group");

        this.membershipRepository.deleteAll(
                this.membershipRepository.findByGroup(group)
        );

        this.groupRepository.delete(group);
    }

    public List<ParticipantMembershipDetailsDTO> getParticipantsFromGroup(String groupId) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        List<Membership> memberships = this.membershipRepository.findByGroup(group);

        return memberships.stream().map(
                membership -> new ParticipantMembershipDetailsDTO(
                        membership.getParticipant(),
                        membership.getNickname()
                )
        ).toList();
    }

    public List<ParticipantMembershipDetailsDTO> addParticipants(
            String groupId,
            List<AddParticipantInGroupRequestDTO> request
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

                String email = FieldValidator.isNullOrBlank(participant.email())
                        ? null
                        : participant.email();
                String phoneNumber = participant.phoneNumber() == null
                        ? null
                        : FieldValidator.isNullOrBlank(participant.phoneNumber().number())
                            ? null
                            : participant.phoneNumber().number();

                if (email == null && phoneNumber == null)
                    throw new BadRequestException("E-mail or phone number required");

                if (email != null) {
                    if (!FieldValidator.isEmailValid(email)) throw new BadRequestException("Invalid e-mail");
                }

                if (phoneNumber != null && participant.phoneNumber().countryCode() != null) {
                    if (!FieldValidator.isPhoneNumberValid(participant.phoneNumber()))
                        throw new BadRequestException("Invalid phone number");
                }

                newParticipant.setId(IdProvider.generateCUID());
                newParticipant.setEmail(email);
                newParticipant.setPhoneNumber(phoneNumber);
                newParticipant.setPassword(null);
                newParticipant.setAccessType(AccessType.GUEST);
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

        List<Membership> memberships = this.membershipRepository.findByGroup(group);

        return memberships.stream().map(
                membership -> new ParticipantMembershipDetailsDTO(
                        membership.getParticipant(),
                        membership.getNickname()
                )
        ).toList();
    }

    public ParticipantMembershipDetailsDTO updateParticipant(
            String groupId,
            String participantId,
            AddParticipantInGroupRequestDTO request
    ) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!group.getOwner().equals(authParticipant))
            throw new ForbiddenException("You don't have permission to update users in this group");

        Participant participant = this.participantRepository.findById(participantId).orElseThrow(
                () -> new NotFoundException("Participant not found with id: " + participantId)
        );

        Membership membership = this.membershipRepository.findByParticipantAndGroup(participant, group)
                .orElseThrow(() -> new BadRequestException("Participant is not in this group"));

        if (participant.getAccessType() == AccessType.USER)
            throw new BadRequestException("You cannot update e-mail or phone number of a registered user");

        Optional<Participant> existingParticipant = this.participantRepository.findByEmail(request.email());

        if (existingParticipant.isPresent() && !existingParticipant.get().getId().equals(participantId))
            throw new ConflictException("Unavailable e-mail");

        existingParticipant = this.participantRepository.findByPhoneNumber(request.phoneNumber().number());
        if (existingParticipant.isPresent() && !existingParticipant.get().getId().equals(participantId))
            throw new ConflictException("Unavailable phone number");

        if (request.email() != null) {
            if (!FieldValidator.isEmailValid(request.email())) throw new BadRequestException("Invalid e-mail");
        }

        if (request.phoneNumber().number() != null && request.phoneNumber().countryCode() != null) {
            if (!FieldValidator.isPhoneNumberValid(request.phoneNumber()))
                throw new BadRequestException("Invalid phone number");
        }

        participant.setEmail(request.email());
        participant.setPhoneNumber(request.phoneNumber().number());
        this.participantRepository.save(participant);

        membership.setNickname(request.nickname());
        this.membershipRepository.save(membership);

        return new ParticipantMembershipDetailsDTO(participant, membership.getNickname());
    }

    public void removeParticipant(String groupId, String participantId) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!group.getOwner().equals(authParticipant))
            throw new ForbiddenException("You don't have permission to remove participants from this group");

        Participant participant = this.participantRepository.findById(participantId).orElseThrow(
                () -> new NotFoundException("Participant not found with id: " + participantId)
        );

        Membership membership = this.membershipRepository.findByParticipantAndGroup(participant, group)
                .orElseThrow(() -> new BadRequestException("Participant is not in this group"));

        this.membershipRepository.delete(membership);
    }

}

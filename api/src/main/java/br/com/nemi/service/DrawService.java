package br.com.nemi.service;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.draw.dto.CreateDrawRequestDTO;
import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.membership.Membership;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.ParticipantMembershipDetailsDTO;
import br.com.nemi.domain.result.Result;
import br.com.nemi.domain.result.dto.ResultDetailsDTO;
import br.com.nemi.domain.result.dto.ResultMessageDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.exception.ForbiddenException;
import br.com.nemi.exception.InternalServerErrorException;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.DrawRepository;
import br.com.nemi.repository.GroupRepository;
import br.com.nemi.repository.MembershipRepository;
import br.com.nemi.repository.ResultRepository;
import br.com.nemi.util.IdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class DrawService {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Draw> getDraws(String groupId) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        return this.drawRepository.findByGroup(group);
    }

    public Draw getDraw(String groupId, String drawId) {
        this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        return this.drawRepository.findById(drawId).orElseThrow(
                () -> new NotFoundException("Draw not found with id: " + drawId)
        );
    }

    public Draw createDraw(String groupId, CreateDrawRequestDTO request) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        List<Membership> memberships = this.membershipRepository.findByGroup(group);
        if (memberships.size() < 2) throw new BadRequestException("Insufficient number of participants to draw");

        Draw draw = new Draw();
        draw.setId(IdProvider.generateCUID());
        draw.setTitle(request.title());
        draw.setDescription(request.description().orElse(null));
        draw.setBasePrice(request.basePrice().orElse(null));

        LocalDate eventDate = request.eventDate().isPresent()
                ? LocalDate.parse(request.eventDate().get())
                : null;
        draw.setEventDate(eventDate);

        draw.setGroup(group);
        draw.setDrawnAt(LocalDateTime.now());

        this.drawRepository.save(draw);

        HashMap<Participant, String> participants = (HashMap<Participant, String>) memberships.stream()
                .collect(Collectors.toMap(Membership::getParticipant, Membership::getNickname));

        Set<Pair<String, String>> previousPairs = this.getPreviousPairs(group);

        List<Result> results = drawResults(draw, participants.keySet().stream().toList(), previousPairs);

        this.resultRepository.saveAll(results);

        List<ResultMessageDTO> resultMessageDTOs = results.stream().map(
                result -> new ResultMessageDTO(
                        result.getGiver().getEmail(),
                        result.getGiver().getPhoneNumber(),
                        draw.getTitle(),
                        participants.get(result.getGiver()),
                        "http://localhost:3000/results",
                        result.getAccessCode()
                )
        ).toList();

        this.notificationService.notifyAll(resultMessageDTOs);

        return draw;
    }

    public Draw updateDraw(String groupId, String drawId, CreateDrawRequestDTO request) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        Draw draw = this.drawRepository.findById(drawId).orElseThrow(
                () -> new NotFoundException("Draw not found with id: " + drawId)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authParticipant.getId().equals(group.getOwner().getId()))
            throw new ForbiddenException("You don't have permission to update draws in this group");

        draw.setTitle(request.title());
        draw.setDescription(request.description().orElse(draw.getDescription()));
        draw.setBasePrice(request.basePrice().orElse(draw.getBasePrice()));
        LocalDate eventDate = request.eventDate().isPresent()
                ? LocalDate.parse(request.eventDate().get())
                : draw.getEventDate();
        draw.setEventDate(eventDate);

        return this.drawRepository.save(draw);
    }

    public void retry(String groupId, String drawId) {

        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        Draw draw = this.drawRepository.findById(drawId).orElseThrow(
                () -> new NotFoundException("Draw not found with id: " + drawId)
        );

        Participant authParticipant =
                (Participant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authParticipant.getId().equals(group.getOwner().getId()))
            throw new ForbiddenException("You don't have permission to create draws in this group");

        List<Membership> memberships = this.membershipRepository.findByGroup(group);
        if (memberships.size() < 2) throw new BadRequestException("Insufficient number of participants to draw");

        this.resultRepository.deleteAllByDraw(draw);

        HashMap<Participant, String> participants = (HashMap<Participant, String>) memberships.stream()
                .collect(Collectors.toMap(Membership::getParticipant, Membership::getNickname));

        Set<Pair<String, String>> previousPairs = this.getPreviousPairs(group);

        List<Result> results = drawResults(draw, participants.keySet().stream().toList(), previousPairs);

        this.resultRepository.saveAll(results);

        List<ResultMessageDTO> resultMessageDTOs = results.stream().map(
                result -> new ResultMessageDTO(
                        result.getGiver().getEmail(),
                        result.getGiver().getPhoneNumber(),
                        draw.getTitle(),
                        participants.get(result.getGiver()),
                        "http://localhost:3000/results",
                        result.getAccessCode()
                )
        ).toList();

        this.notificationService.notifyAll(resultMessageDTOs);
    }

    public ResultDetailsDTO getResults(String groupId, String drawId, String accessCode) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        Draw draw = this.drawRepository.findById(drawId).orElseThrow(
                () -> new NotFoundException("Draw not found with id: " + drawId)
        );

        Object authParticipant = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Result result;
        if (authParticipant instanceof Participant) {
            result = this.resultRepository.findByDrawAndGiver(draw, (Participant) authParticipant).orElseThrow(
                    () -> new NotFoundException("Result not found for this draw")
            );
        } else {
            if (accessCode == null || accessCode.isBlank())
                throw new ForbiddenException("Cannot show results without access code");

            result = this.resultRepository.findByAccessCode(accessCode).orElseThrow(
                    () -> new NotFoundException("Result not found for this draw")
            );
        }

        ParticipantMembershipDetailsDTO giver = new ParticipantMembershipDetailsDTO(
                result.getGiver(),
                this.membershipRepository.findByParticipantAndGroup(result.getGiver(), group).orElseThrow(
                        () -> new BadRequestException("Participant is not in group")
                ).getNickname()
        );

        ParticipantMembershipDetailsDTO receiver = new ParticipantMembershipDetailsDTO(
                result.getReceiver(),
                this.membershipRepository.findByParticipantAndGroup(result.getReceiver(), group).orElseThrow(
                        () -> new BadRequestException("Participant is not in group")
                ).getNickname()
        );

        return new ResultDetailsDTO(
                result.getId(),
                result.getAccessCode(),
                result.getDraw(),
                giver,
                receiver
        );
    }

    private List<Result> drawResults(
            Draw draw,
            List<Participant> participants,
            Set<Pair<String, String>> previousPairs
    ) {

        List<Result> results = new ArrayList<>();
        Set<Participant> alreadyDrawn = new HashSet<>();
        Random random = ThreadLocalRandom.current();

        int maxAttempts = 1000;
        int attempts;

        for (int i = 0; i < participants.size(); i++) {

            Participant giver = participants.get(i);

            attempts = 0;

            while (attempts < maxAttempts) {
                int j = random.nextInt(participants.size());
                Participant receiver = participants.get(j);

                if (giver.getId().equals(receiver.getId()) ||
                        alreadyDrawn.contains(receiver) ||
                        previousPairs.contains(Pair.of(giver.getId(), receiver.getId()))
                ) {
                    attempts++;
                    continue;
                }

                alreadyDrawn.add(receiver);

                Result result = new Result();
                result.setAccessCode(IdProvider.generateNanoId(8));
                result.setDraw(draw);
                result.setGiver(giver);
                result.setReceiver(receiver);

                results.add(result);

                break;
            }

            if (attempts == maxAttempts) throw new InternalServerErrorException("Unable to draw after " + attempts + " attempts");
        }

        return results;
    }

    private Set<Pair<String, String>> getPreviousPairs(Group group) {
        List<Draw> previousDraws = this.drawRepository.findByGroup(group);

        List<Result> previousResults = new ArrayList<>();

        previousDraws.forEach(d -> previousResults.addAll(this.resultRepository.findByDraw(d)));

        return previousResults.stream()
                .map(r -> Pair.of(r.getGiver().getId(), r.getReceiver().getId()))
                .collect(Collectors.toSet());
    }
}

package br.com.nemi.domain.group.dto;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.participant.Participant;

import java.time.LocalDateTime;
import java.util.Set;

public record GroupDetailsResponseDTO(
        String id,
        String name,
        Participant owner,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<Participant> participants
) {

    public GroupDetailsResponseDTO(Group group) {
        this(
                group.getId(),
                group.getName(),
                group.getOwner(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.getParticipants()
        );
    }
}

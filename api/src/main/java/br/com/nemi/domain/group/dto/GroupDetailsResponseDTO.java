package br.com.nemi.domain.group.dto;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.participant.Participant;

import java.time.LocalDateTime;
import java.util.List;

public record GroupDetailsResponseDTO(
        String id,
        String name,
        Participant owner,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Participant> participants
) {

    public GroupDetailsResponseDTO(Group group, List<Participant> participants) {
        this(
                group.getId(),
                group.getName(),
                group.getOwner(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                participants
        );
    }
}

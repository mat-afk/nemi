package br.com.nemi.domain.group.dto;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.ParticipantMembershipDetailsDTO;

import java.time.LocalDateTime;
import java.util.List;

public record GroupDetailsDTO(
        String id,
        String name,
        Participant owner,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ParticipantMembershipDetailsDTO> participants
) {

    public GroupDetailsDTO(Group group, List<ParticipantMembershipDetailsDTO> participants) {
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

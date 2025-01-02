package br.com.nemi.domain.participant.dto;

import br.com.nemi.domain.participant.Participant;

import java.time.LocalDateTime;

public record ParticipantMembershipDetailsDTO(
        String id,
        String nickname,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public ParticipantMembershipDetailsDTO(Participant participant, String nickname) {
        this(
                participant.getId(),
                nickname,
                participant.getEmail(),
                participant.getPhoneNumber(),
                participant.getCreatedAt(),
                participant.getUpdatedAt()
        );
    }
}

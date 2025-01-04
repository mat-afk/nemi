package br.com.nemi.domain.participant.dto;

public record AddParticipantInGroupRequestDTO(
        String nickname,
        String email,
        String phoneNumber
) {
}

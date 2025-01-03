package br.com.nemi.domain.participant.dto;

public record AddParticipantRequestDTO(
        String nickname,
        String email,
        String phoneNumber
) {
}

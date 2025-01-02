package br.com.nemi.domain.participant.dto;

public record CreateParticipantRequestDTO(
        String nickname,
        String email,
        String phoneNumber
) {
}

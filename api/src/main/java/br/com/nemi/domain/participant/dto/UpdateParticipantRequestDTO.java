package br.com.nemi.domain.participant.dto;

public record UpdateParticipantRequestDTO(
        String email,
        String phoneNumber
) {
}

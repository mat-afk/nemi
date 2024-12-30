package br.com.nemi.domain.participant.dto;

public record CreateParticipantRequestDTO(
        String name,
        String email,
        String phoneNumber
) {
}

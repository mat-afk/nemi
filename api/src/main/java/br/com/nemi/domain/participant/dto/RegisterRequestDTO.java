package br.com.nemi.domain.participant.dto;

public record RegisterRequestDTO(
        String email,
        String phoneNumber,
        String password
) {
}

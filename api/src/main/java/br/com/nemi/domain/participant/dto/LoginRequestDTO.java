package br.com.nemi.domain.participant.dto;

public record LoginRequestDTO(
        String email,
        String phoneNumber,
        String password
) {
}

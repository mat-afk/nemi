package br.com.nemi.domain.participant.dto;

public record RegisterRequestDTO(
        String email,
        PhoneNumberDTO phoneNumber,
        String password
) {
}

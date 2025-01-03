package br.com.nemi.domain.participant.dto;

public record LoginRequestDTO(
        String login,
        String password
) {
}

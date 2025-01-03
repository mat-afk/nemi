package br.com.nemi.domain.participant.dto;

import br.com.nemi.domain.participant.Participant;

public record AuthenticationResponseDTO(
        Participant participant,
        String token
) {
}

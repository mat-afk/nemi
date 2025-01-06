package br.com.nemi.domain.group.dto;

import br.com.nemi.domain.participant.dto.PhoneNumberDTO;

public record AddParticipantInGroupRequestDTO(
        String nickname,
        String email,
        PhoneNumberDTO phoneNumber
) {
}

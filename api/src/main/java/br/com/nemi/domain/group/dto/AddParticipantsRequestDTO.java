package br.com.nemi.domain.group.dto;

import br.com.nemi.domain.participant.dto.CreateParticipantRequestDTO;

import java.util.List;

public record AddParticipantsRequestDTO(
        List<CreateParticipantRequestDTO> participants
) {
}

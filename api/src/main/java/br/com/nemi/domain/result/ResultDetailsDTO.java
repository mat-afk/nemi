package br.com.nemi.domain.result;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.participant.dto.ParticipantMembershipDetailsDTO;

public record ResultDetailsDTO(
        Long id,
        String accessCode,
        Draw draw,
        ParticipantMembershipDetailsDTO giver,
        ParticipantMembershipDetailsDTO receiver
) {
}

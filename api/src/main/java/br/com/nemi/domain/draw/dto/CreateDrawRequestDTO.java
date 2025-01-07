package br.com.nemi.domain.draw.dto;

import java.math.BigDecimal;
import java.util.Optional;

public record CreateDrawRequestDTO(
        String title,
        Optional<String> description,
        Optional<BigDecimal> basePrice,
        Optional<String> eventDate
) {
}

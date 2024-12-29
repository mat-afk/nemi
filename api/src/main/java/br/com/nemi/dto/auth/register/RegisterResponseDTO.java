package br.com.nemi.dto.auth.register;

import java.time.LocalDateTime;

public record RegisterResponseDTO(
    String userId,
    String name,
    String email,
    String phone,
    String description,
    Boolean verified,
    LocalDateTime createdAt
) {
}

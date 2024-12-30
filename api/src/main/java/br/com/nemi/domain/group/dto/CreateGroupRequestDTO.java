package br.com.nemi.domain.group.dto;

public record CreateGroupRequestDTO(
        String name,
        String ownerId
) {
}

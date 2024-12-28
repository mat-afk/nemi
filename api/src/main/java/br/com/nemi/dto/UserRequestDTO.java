package br.com.nemi.dto;

public record UserRequestDTO(
        String name,
        String email,
        String phone,
        String password,
        String description
) {
}

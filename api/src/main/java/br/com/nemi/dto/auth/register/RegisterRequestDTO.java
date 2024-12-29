package br.com.nemi.dto.auth.register;

public record RegisterRequestDTO(
    String name,
    String email,
    String phone,
    String password,
    String description
) {
}

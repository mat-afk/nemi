package br.com.nemi.domain.result.dto;

public record ResultMessageDTO(
        String email,
        String phoneNumber,
        String drawTitle,
        String nickname,
        String url,
        String accessCode
) {
}

package br.com.nemi.controller;

import br.com.nemi.dto.UserRequestDTO;
import br.com.nemi.dto.UserResponseDTO;
import br.com.nemi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody UserRequestDTO body,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        UserResponseDTO userResponseDTO = this.userService.createUser(body);

        return ResponseEntity.ok(userResponseDTO);
    }
}

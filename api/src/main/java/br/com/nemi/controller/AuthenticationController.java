package br.com.nemi.controller;

import br.com.nemi.dto.auth.register.RegisterRequestDTO;
import br.com.nemi.dto.auth.register.RegisterResponseDTO;
import br.com.nemi.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @RequestBody RegisterRequestDTO request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        RegisterResponseDTO response = this.authenticationService.register(request);

        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(response.userId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }
}

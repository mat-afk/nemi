package br.com.nemi.controller;

import br.com.nemi.domain.participant.dto.AuthenticationResponseDTO;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.service.AuthenticationService;
import br.com.nemi.util.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AuthenticationResponseDTO response = this.authenticationService.register(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginRequestDTO request) {
        if (FieldValidator.isNullOrBlank(request.login()))
            throw new BadRequestException("E-mail or phone number are required");

        if (FieldValidator.isNullOrBlank(request.password()))
            throw new BadRequestException("Password is required");

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        AuthenticationResponseDTO response = this.authenticationService.login(authentication);

        return ResponseEntity.ok().body(response);
    }
}

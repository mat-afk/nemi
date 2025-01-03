package br.com.nemi.controller;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity<Participant> register(@RequestBody RegisterRequestDTO request) {
        Participant response = this.authenticationService.register(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {
        var usernamePassword = this.authenticationService.login(request);
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }
}

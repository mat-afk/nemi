package br.com.nemi.controller;

import br.com.nemi.domain.participant.dto.AuthenticationResponseDTO;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.service.AuthenticationService;
import br.com.nemi.util.FieldValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegisterRequestDTO request,
            HttpServletResponse response
    ) {
        AuthenticationResponseDTO body = this.authenticationService.register(request);

        Cookie cookie = new Cookie("jwt", body.token());
        configureCookie(cookie);
        response.addCookie(cookie);

        return ResponseEntity.created(URI.create("")).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        if (FieldValidator.isNullOrBlank(request.login()))
            throw new BadRequestException("E-mail or phone number are required");

        if (FieldValidator.isNullOrBlank(request.password()))
            throw new BadRequestException("Password is required");

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        AuthenticationResponseDTO body = this.authenticationService.login(authentication);

        Cookie cookie = new Cookie("jwt", body.token());
        configureCookie(cookie);
        response.addCookie(cookie);

        return ResponseEntity.ok().body(body);
    }

    private void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
    }
}

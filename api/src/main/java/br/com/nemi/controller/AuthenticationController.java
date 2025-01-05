package br.com.nemi.controller;

import br.com.nemi.domain.participant.dto.AuthenticationResponseDTO;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.service.AuthenticationService;
import br.com.nemi.util.FieldValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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

        return ResponseEntity.ok().body(body);
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie :cookies) {
                if (cookie.getName().equals("jwt")) {
                    Cookie cookieToDelete = new Cookie(cookie.getName(), null);
                    cookieToDelete.setHttpOnly(true);
                    cookieToDelete.setSecure(true);
                    cookieToDelete.setPath("/");
                    cookieToDelete.setMaxAge(0);
                    response.addCookie(cookieToDelete);
                }
            }
        }

        SecurityContextHolder.getContext().setAuthentication(null);

        return ResponseEntity.ok().build();
    }

    private void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
    }
}

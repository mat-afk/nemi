package br.com.nemi.config.security;

import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.ParticipantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final List<String> PUBLIC_ROUTES_RAW = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/logout"
    );

    private final List<String> PUBLIC_ROUTES_PATTERN = List.of("/groups/\\w/draws/\\w/results/\\w");

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = this.recoverToken(request);

        if (token != null) {
            String username = this.tokenService.validateToken(token);
            UserDetails user = this.participantRepository
                    .findByEmailOrPhoneNumber(username, username)
                    .stream().findFirst()
                    .orElseThrow(() -> new NotFoundException("Participant not found with: " + username));

            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    user.getPassword(),
                    user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) return cookie.getValue();
            }
        }

        var header = request.getHeader("Authorization");
        if (header != null) return header.replace("Bearer ", "");

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api", "");
        return PUBLIC_ROUTES_RAW.contains(path) || PUBLIC_ROUTES_PATTERN.stream().anyMatch(path::matches);
    }

}

package br.com.nemi.config.security;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.exception.InternalServerErrorException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    
    @Value("${api.security.jwt.secret}")
    private String JWT_SECRET;

    private final String ISSUER = "nemi";

    public String generateToken(Participant participant) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(participant.getUsername())
                    .withExpiresAt(getExpirationInstant())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new InternalServerErrorException("Error while generating token: " + exception.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);

            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            throw new InternalServerErrorException("Error while validating token: " + exception.getMessage());
        }
    }

    private Instant getExpirationInstant() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

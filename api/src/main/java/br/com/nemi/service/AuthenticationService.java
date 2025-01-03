package br.com.nemi.service;

import br.com.nemi.config.security.TokenService;
import br.com.nemi.domain.participant.AccessType;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.AuthenticationResponseDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.ConflictException;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.FieldValidator;
import br.com.nemi.util.IdentifierProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.participantRepository
                .findByEmailOrPhoneNumber(username, username)
                .orElseThrow(() -> new NotFoundException("User not found with: " + username));
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        String email = FieldValidator.isNullOrBlank(request.email()) ? null : request.email();
        String phoneNumber = FieldValidator.isNullOrBlank(request.phoneNumber()) ? null : request.phoneNumber();

        Optional<Participant> existingParticipant =
                this.participantRepository.findByEmailOrPhoneNumber(email, phoneNumber);

        Participant participant;

        if (existingParticipant.isPresent()) {
            participant = existingParticipant.get();

            if (participant.getAccessType() == AccessType.USER)
                throw new ConflictException("Unavailable e-mail or phone number");

            String hashedPassword = new BCryptPasswordEncoder().encode(request.password());
            participant.setPassword(hashedPassword);
            participant.setAccessType(AccessType.USER);

        } else {

            participant = new Participant();
            participant.setId(IdentifierProvider.generateCUID());
            participant.setEmail(email);
            participant.setPhoneNumber(phoneNumber);

            String hashedPassword = new BCryptPasswordEncoder().encode(request.password());
            participant.setPassword(hashedPassword);

            participant.setAccessType(AccessType.USER);

            LocalDateTime now = LocalDateTime.now();
            participant.setCreatedAt(now);
            participant.setUpdatedAt(now);
        }

        this.participantRepository.save(participant);

        String token = this.tokenService.generateToken(participant);

        return new AuthenticationResponseDTO(participant, token);
    }

    public AuthenticationResponseDTO login(Authentication authentication) {
        Participant participant = (Participant) authentication.getPrincipal();
        String token = this.tokenService.generateToken(participant);

        return new AuthenticationResponseDTO(participant, token);
    }

}

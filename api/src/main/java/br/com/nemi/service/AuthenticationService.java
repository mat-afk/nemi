package br.com.nemi.service;

import br.com.nemi.config.security.TokenService;
import br.com.nemi.domain.participant.AccessType;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.AuthenticationResponseDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.exception.ConflictException;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.FieldValidator;
import br.com.nemi.util.IdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.participantRepository.findByEmailOrPhoneNumber(username, username)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Participant not found with: " + username)
        );
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        String email = FieldValidator.isNullOrBlank(request.email()) ? null : request.email();

        String phoneNumber = request.phoneNumber() == null
                ? null
                : FieldValidator.isNullOrBlank(request.phoneNumber().number())
                    ? null
                    : request.phoneNumber().number();

        if (email == null && phoneNumber == null) throw new BadRequestException("E-mail or phone number required");

        if (email != null) {
            if (!FieldValidator.isEmailValid(email)) throw new BadRequestException("Invalid e-mail");
        }

        if (request.phoneNumber() != null) {
            if (!FieldValidator.isPhoneNumberValid(request.phoneNumber()))
                throw new BadRequestException("Invalid phone number");
        }

        List<Participant> existingParticipants = this.participantRepository.findByEmailOrPhoneNumber(email, phoneNumber);
        if (existingParticipants.size() > 1) throw new ConflictException("Unavailable e-mail and/or phone number");

        Optional<Participant> existingParticipant = existingParticipants.stream().findFirst();

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
            participant.setId(IdProvider.generateCUID());
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

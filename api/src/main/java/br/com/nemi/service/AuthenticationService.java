package br.com.nemi.service;

import br.com.nemi.domain.participant.AccessType;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.domain.participant.dto.RegisterRequestDTO;
import br.com.nemi.exception.BadRequestException;
import br.com.nemi.exception.ConflictException;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.ParticipantRepository;
import br.com.nemi.util.FieldValidator;
import br.com.nemi.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant participant = this.participantRepository
                .findByEmailOrPhoneNumber(username, username)
                .orElseThrow(() -> new NotFoundException("User not found with: " + username));

        return new User(username, participant.getPassword(), List.of());
    }

    public Participant register(RegisterRequestDTO request) {
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
            participant.setId(TokenGenerator.generateCUID());
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

        return participant;
    }

    public UsernamePasswordAuthenticationToken login(LoginRequestDTO request) {
        if (FieldValidator.isNullOrBlank(request.login()))
            throw new BadRequestException("E-mail or phone number are required");

        if (FieldValidator.isNullOrBlank(request.password()))
            throw new BadRequestException("Password is required");

        return new UsernamePasswordAuthenticationToken(request.login(), request.password());
    }

}

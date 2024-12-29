package br.com.nemi.service;

import br.com.nemi.domain.user.User;
import br.com.nemi.domain.user.exception.EmailAlreadyInUseException;
import br.com.nemi.domain.user.exception.PhoneAlreadyInUseException;
import br.com.nemi.dto.auth.register.RegisterRequestDTO;
import br.com.nemi.dto.auth.register.RegisterResponseDTO;
import br.com.nemi.repository.UserRepository;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import cool.graph.cuid.Cuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public RegisterResponseDTO register(RegisterRequestDTO request) {

        Optional<User> existingUser = this.userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) throw new EmailAlreadyInUseException("E-mail already in use");

        existingUser = this.userRepository.findByPhone(request.phone());
        if (existingUser.isPresent()) throw new PhoneAlreadyInUseException("Phone already in use");

        User user = new User();

        user.setId(Cuid.createCuid());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(request.password());
        user.setDescription(request.description());
        user.setVerified(false);
        user.setVerificationToken(this.generateVerificationToken());
        user.setCreatedAt(LocalDateTime.now());

        this.userRepository.save(user);

        return new RegisterResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDescription(),
                user.getVerified(),
                user.getCreatedAt()
        );
    }

    private String generateVerificationToken() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                8
        );
    }
}

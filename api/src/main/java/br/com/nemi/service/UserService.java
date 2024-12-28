package br.com.nemi.service;

import br.com.nemi.domain.user.User;
import br.com.nemi.dto.UserRequestDTO;
import br.com.nemi.dto.UserResponseDTO;
import br.com.nemi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User newUser = new User();

        newUser.setId("1");
        newUser.setName(userRequestDTO.name());
        newUser.setEmail(userRequestDTO.email());
        newUser.setPhone(userRequestDTO.phone());
        newUser.setPassword(userRequestDTO.password());
        newUser.setDescription(userRequestDTO.description());
        newUser.setVerified(false);
        newUser.setVerificationToken("1234");
        newUser.setCreatedAt(LocalDateTime.now());

        this.userRepository.save(newUser);

        return new UserResponseDTO(newUser.getId());
    }
}

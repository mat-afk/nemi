package br.com.nemi.service;

import br.com.nemi.domain.participant.AccessType;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.participant.dto.LoginRequestDTO;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private ParticipantRepository participantRepository;

    public String login(LoginRequestDTO request) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant participant = this.participantRepository
                .findByEmailOrPhoneNumber(username, username)
                .orElseThrow(() -> new NotFoundException("User not found with: " + username));

        List<SimpleGrantedAuthority> authorities;

        if (participant.getAccessType() == AccessType.USER)
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_GUEST"));
        else
            authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));

        return new User(username, participant.getPassword(), authorities);
    }

}

package faang.school.urlshortenerservice.service.auth;

import faang.school.urlshortenerservice.dto.JwtRequest;
import faang.school.urlshortenerservice.dto.JwtResponse;
import faang.school.urlshortenerservice.generator.JwtTokenGenerator;
import faang.school.urlshortenerservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    public JwtResponse createAuthToken(JwtRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenGenerator.generateToken(userDetails);

        return new JwtResponse(token);
    }
}

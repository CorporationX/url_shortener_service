package faang.school.urlshortenerservice.service.auth;

import faang.school.urlshortenerservice.dto.JwtRequest;
import faang.school.urlshortenerservice.dto.JwtResponse;

public interface AuthService {
    JwtResponse createAuthToken(JwtRequest authRequest);
}

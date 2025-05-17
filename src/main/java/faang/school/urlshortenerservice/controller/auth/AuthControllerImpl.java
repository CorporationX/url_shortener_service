package faang.school.urlshortenerservice.controller.auth;

import faang.school.urlshortenerservice.dto.JwtRequest;
import faang.school.urlshortenerservice.dto.JwtResponse;
import faang.school.urlshortenerservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthControllerImpl implements AuthController{
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<JwtResponse> createAuthToken(JwtRequest authRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authRequest));
    }
}

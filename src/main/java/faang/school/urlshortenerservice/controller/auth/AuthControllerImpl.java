package faang.school.urlshortenerservice.controller.auth;

import faang.school.urlshortenerservice.dto.JwtRequest;
import faang.school.urlshortenerservice.dto.JwtResponse;
import faang.school.urlshortenerservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthControllerImpl {
    private final AuthService authService;

    @PostMapping
    private ResponseEntity<JwtResponse> createAuthToken(@RequestBody JwtRequest authRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authRequest));
    }
}

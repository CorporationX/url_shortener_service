package faang.school.urlshortenerservice.config.context.filter;

import faang.school.urlshortenerservice.generator.JwtTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_HEADER_START = "Bearer ";
    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authHeader = request.getHeader(AUTH_HEADER);
        String userName = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_START)) {
            jwt = authHeader.substring(AUTH_HEADER_START.length());
            userName = jwtTokenGenerator.getUsername(jwt);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userName,
                    null,
                    jwtTokenGenerator.getRoles(jwt).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }
}

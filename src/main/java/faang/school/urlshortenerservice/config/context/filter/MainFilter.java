package faang.school.urlshortenerservice.config.context.filter;

import faang.school.urlshortenerservice.generator.JwtTokenGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class MainFilter extends OncePerRequestFilter {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final FilterExceptionHandler filterExceptionHandler;
    private final List<RequestMatcher> matchers = new ArrayList<>();
    private JwtRequestFilter requestFilter;
    private final List<RequestMatcher> urlEndpointMatchers = List.of(
            new AntPathRequestMatcher("/api/*/urls")
    );

    @PostConstruct
    private void init() {
        requestFilter = new JwtRequestFilter(jwtTokenGenerator);
        matchers.addAll(urlEndpointMatchers);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            boolean matched = matchers.stream().anyMatch(matcher -> matcher.matches(request));
            log.info("Request to [{}] matched: {}", request.getRequestURI(), matched);
            if (matched) {
                requestFilter.doFilter(request, response, filterChain);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            filterExceptionHandler.handleException(request, response, ex);
        }
    }
}

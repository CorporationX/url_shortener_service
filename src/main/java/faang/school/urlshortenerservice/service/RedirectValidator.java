package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.properties.RedirectValidationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedirectValidator {

    private final MetricsService metricsService;
    private final RedirectValidationProperties redirectValidationProperties;

    /**
     * Validates URL before redirect to protect against Open Redirect Attack
     * Defense in Depth: validation even if URL was already checked during creation
     */
    public void validateRedirectUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new InvalidUrlException("Redirect URL cannot be null or empty");
        }

        try {
            URI uri = new URI(url);

            String scheme = uri.getScheme();
            if (scheme == null || 
                (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                metricsService.redirectValidationFailureCounter("invalid_scheme").increment();
                throw new InvalidUrlException("Invalid redirect scheme: " + scheme + ". Only HTTP and HTTPS are allowed");
            }

            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                throw new InvalidUrlException("Invalid redirect host: host cannot be empty");
            }

            if (host.contains("..") || host.contains("//")) {
                throw new InvalidUrlException("Invalid redirect host format: " + host);
            }

            if (isPrivateHost(host)) {
                log.warn("Attempted redirect to private IP/localhost: {}", host);
                metricsService.redirectValidationFailureCounter("private_ip").increment();
                throw new InvalidUrlException("Private IPs and localhost are not allowed for redirects: " + host);
            }

            if (isBlacklisted(host)) {
                log.warn("Attempted redirect to blacklisted domain: {}", host);
                metricsService.redirectValidationFailureCounter("blacklisted_domain").increment();
                throw new InvalidUrlException("Blacklisted domain: " + host);
            }

        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Invalid redirect URL format: " + url, e);
        }
    }

    /**
     * Checks if host is a private IP or localhost
     * Uses InetAddress for reliable checking of all cases
     */
    private boolean isPrivateHost(String host) {

        if ("localhost".equalsIgnoreCase(host)) {
            return true;
        }

        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr.isLoopbackAddress() ||      // 127.0.0.1, ::1
                   addr.isSiteLocalAddress() ||     // 10.x, 192.168.x, 172.16-31.x
                   addr.isLinkLocalAddress() ||     // 169.254.x.x
                   addr.isAnyLocalAddress();        // 0.0.0.0
        } catch (UnknownHostException e) {
            // DNS lookup failed - cannot check via InetAddress
            // Fallback to string-based check for private ranges
            log.debug("Could not resolve host for private IP check: {}", host);
            return host.startsWith("192.168.") ||
                   host.startsWith("10.") ||
                   (host.startsWith("172.") && isPrivate172Range(host));
        }
    }

    /**
     * Checks if IP is in range 172.16.0.0 - 172.31.255.255
     * Used as fallback if InetAddress could not resolve host
     */
    private boolean isPrivate172Range(String host) {
        try {
            String[] parts = host.split("\\.");
            if (parts.length >= 2) {
                int secondOctet = Integer.parseInt(parts[1]);
                return secondOctet >= 16 && secondOctet <= 31;
            }
        } catch (NumberFormatException e) {
            // Not an IP address, ignore
        }
        return false;
    }

    /**
     * Checks if host is in blacklist
     * Supports subdomain checking (e.g., subdomain.evil.com is blocked if evil.com is in blacklist)
     */
    private boolean isBlacklisted(String host) {
        String hostLower = host.toLowerCase();
        return redirectValidationProperties.getBlacklistedDomains().stream()
                .anyMatch(blacklisted -> {
                    String blacklistedLower = blacklisted.toLowerCase();

                    return hostLower.equals(blacklistedLower) ||
                           hostLower.endsWith("." + blacklistedLower);
                });
    }
}


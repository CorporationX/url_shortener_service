package faang.school.urlshortenerservice.dto;

import java.net.URI;

public record RedirectResponse(
        URI url
) {
}

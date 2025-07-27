package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {
    private URI url;
}

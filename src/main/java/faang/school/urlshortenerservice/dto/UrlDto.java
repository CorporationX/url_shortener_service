package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlDto {

    @NotNull(message = "URL must not be empty")
    @Pattern(
            regexp = "((?<SCHEME>https?)(?<HOSTNAME>://(?:www.|[a-zA-Z.]+)[a-zA-Z0-9\\-.]+\\.(?:biz|ca|com|edu|gov|info|me|mil|museum|name|net|org|uk|ru|us))?(?<PORT>:[0-9][0-9]{0,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])?(?<PATH>[a-zA-Z0-9\\-./]+)?(?<QUERY>\\?$|[a-zA-Z0-9.,;?'\\\\+&%$=~_\\-*]+)?(?<FRAGMENT>#[a-zA-Z0-9\\-.]+)?)",
            message = "URL must be valid"
    )
    private String url;
}

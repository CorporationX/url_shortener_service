package faang.school.urlshortenerservice.config.sequence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "number-sequence")
public class NumberSequenceProperties {

    private int generationBatch;
}

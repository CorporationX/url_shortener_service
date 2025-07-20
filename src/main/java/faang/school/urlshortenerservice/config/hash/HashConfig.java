package faang.school.urlshortenerservice.config.hash;

import io.seruco.encoding.base62.Base62;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.ByteBuffer;

@EnableAsync
@Configuration
public class HashConfig {

    @Bean
    public Base62 base62() {
        return Base62.createInstance();
    }
}

package faang.school.urlshortenerservice.config;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlValidateConfiguration {
    @Bean
    public UrlValidator urlValidator(){
        return new UrlValidator(new String[]{"http", "https"});
    }
}

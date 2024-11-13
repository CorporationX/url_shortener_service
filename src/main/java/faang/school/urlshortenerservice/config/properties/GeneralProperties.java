package faang.school.urlshortenerservice.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralProperties {
    @Value("${app.protocol}")
    public String protocol;

    @Value("${app.host}")
    public String host;

    @Value("${server.port}")
    public String port;

    public String getAppUrl() {
        return String.format("%s://%s:%s", protocol, host, port);
    }
}

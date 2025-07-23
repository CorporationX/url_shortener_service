package faang.school.urlshortenerservice.util;

import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class Utils {
    public String format(final String messagePattern, final Object arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object arg1, final Object arg2) {
        return new FormattedMessage(messagePattern, arg1, arg2).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object... arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }

    public boolean isUrlValid(final String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}

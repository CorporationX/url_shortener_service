package faang.school.urlshortenerservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.util.ContainersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlShortenerIntegrationTest extends ContainersConfig {

    private static final String USER_PARAMETER = "x-user-id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
}

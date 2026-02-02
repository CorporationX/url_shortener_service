package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.config.context.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
@Import({UserContext.class, UrlExceptionHandler.class})
@AutoConfigureMockMvc
public class UrlExceptionHandlerMvcTest {

    @Autowired
    MockMvc mockMvc;

    private static final int USER_ID = 10;
    private static final String USER_HEADER = "x-user-id";

    @Test
    public void testMethodArgumentNotValidHandling() throws Exception {
        mockMvc.perform(post("/test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(USER_HEADER, USER_ID))
                .andExpect(jsonPath("$.url").value("/test/valid"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name")
                        .value("Name cannot be null, empty or a space"))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    public void testConstraintViolationHandling() throws Exception {
        mockMvc.perform(get("/test/valid/constraint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("/test/valid/constraint"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    public void testUrlNotValidHandling() throws Exception {
        mockMvc.perform(get("/test/url/valid")
                        .header(USER_HEADER, USER_ID))
                .andExpect(jsonPath("$.url").value("/test/url/valid"))
                .andExpect(jsonPath("$.message").value("Invalid URL!"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    public void testUrlNotFoundHandling() throws Exception {
        mockMvc.perform(get("/test/url/hash")
                        .header(USER_HEADER, USER_ID))
                .andExpect(jsonPath("$.url").value("/test/url/hash"))
                .andExpect(jsonPath("$.message").value("The URL for this hash does not exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    public void testInternalErrorsHandling() throws Exception {
        mockMvc.perform(get("/test/internal")
                        .header(USER_HEADER, USER_ID))
                .andExpect(jsonPath("$.url").value("/test/internal"))
                .andExpect(jsonPath("$.message")
                        .value("Internal server error. Something went wrong."))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}

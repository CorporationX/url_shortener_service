package faang.school.urlshortenerservice.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JacksonConfigTest {

    private ObjectMapper customObjectMapper;

    private RedisSerializer serializer;


    @BeforeEach
    public void setUp() {
        customObjectMapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        //mapper.registerModule(new ParameterNamesModule());

        customObjectMapper.activateDefaultTyping(
                customObjectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        /*                mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY);*/

        /*customObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);*/

        /*PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("faang.school.urlshortenerservice")
                        .build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING);*/

        serializer = new GenericJackson2JsonRedisSerializer(customObjectMapper);
    }
    @Test
    @DisplayName("Test dto serialization by objectMapper")
    void testSerializationAndDeserializationByObjectMapper() throws Exception {

        UrlResponseDto dto = UrlResponseDto.builder()
                .hash("NX2LAG")
                .url("http://somesite.ru/long/url/with/some/params?param=value&id=1")
                .shortUrl("http://site.com/NX2LAG")
                .build();

        String json = customObjectMapper.writeValueAsString(dto);
        System.out.println("Serialized JSON: " + json);

        UrlResponseDto deserializedDto = customObjectMapper.readValue(json, UrlResponseDto.class);

        assertEquals(dto.hash(), deserializedDto.hash());
        assertEquals(dto.url(), deserializedDto.url());
        assertEquals(dto.shortUrl(), deserializedDto.shortUrl());
    }

    @Test
    @DisplayName("Test dto serialization by serializer")
    void testSerializationAndDeserializationBySerializer() {



        // Создаем объект для тестирования
        UrlResponseDto dto = UrlResponseDto.builder()
                .hash("NX2LAG")
                .url("http://somesite.ru/long/url/with/some/params?param=value&id=1")
                .shortUrl("http://site.com/NX2LAG")
                .build();

        // Сериализуем объект в байты
        byte[] serialized = serializer.serialize(dto);
        assertNotNull(serialized);

        // Десериализуем байты обратно в объект
        UrlResponseDto deserializedDto = (UrlResponseDto) serializer.deserialize(serialized);

        // Проверяем, что объекты равны
        assertNotNull(deserializedDto);
        assertEquals(dto.hash(), deserializedDto.hash());
        assertEquals(dto.url(), deserializedDto.url());
        assertEquals(dto.shortUrl(), deserializedDto.shortUrl());
    }

    @Test
    void testClassInformationInJson() {

        // Создаем объект для тестирования
        UrlResponseDto dto = UrlResponseDto.builder()
                .hash("NX2LAG")
                .url("http://somesite.ru/long/url/with/some/params?param=value&id=1")
                .shortUrl("http://site.com/NX2LAG")
                .build();

        // Сериализуем объект в байты
        byte[] serialized = serializer.serialize(dto);
        assertNotNull(serialized);

        // Преобразуем байты в строку JSON
        String json = new String(serialized);

        // Проверяем, что JSON содержит информацию о классе
        assertTrue(json.contains("\"@class\":\"faang.school.urlshortenerservice.dto.UrlResponseDto\""));
    }

}
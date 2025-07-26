package faang.school.urlshortenerservice.config.redis.url_hash_cache;

import faang.school.urlshortenerservice.redis.RedisKeyExpirationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class UrlHashCacheRedisConfiguration {

    private final RedisUrlHashCacheProperties properties;

    @Bean("urlHashCacheRedisConnectionFactory")
    public RedisConnectionFactory urlHashCacheRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.host);
        config.setPort(properties.port);

        return new LettuceConnectionFactory(config);
    }

    @Bean("urlHashCacheRedisTemplate")
    public RedisTemplate<String, String> urlHashCacheRedisTemplate(
            @Qualifier("urlHashCacheRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("urlHashCacheRedisConnectionFactory") RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter // Это адаптер для нашего слушателя
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Подписываемся на канал событий истечения срока действия ключей
        // __keyevent@<db>__:expired
        // @0 означает базу данных по умолчанию (DB 0).
        container.addMessageListener(listenerAdapter, new ChannelTopic("__keyevent@0__:expired"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisKeyExpirationListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public RedisKeyExpirationListener redisKeyExpirationListener(RedisTemplate<String, String> urlHashCacheRedisTemplate) {
        return new RedisKeyExpirationListener(urlHashCacheRedisTemplate);
    }
}
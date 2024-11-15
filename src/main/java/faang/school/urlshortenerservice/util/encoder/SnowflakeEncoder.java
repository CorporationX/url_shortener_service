package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.downgoon.snowflake.Snowflake;

@Component
@RequiredArgsConstructor
public class SnowflakeEncoder extends Encoder<Long, Hash> {

    private final Snowflake snowflake;

    @Override
    public Hash encode(Long aLong) {
        String hash = Long.toString(snowflake.nextId());
        return new Hash(hash);
    }
}

package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.model.Hash;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class Base62EncoderImpl implements Base62Encoder{

    private final HashMapper hashMapper;

    @Override
    public List<Hash> encode(List<Integer> numbers) {
        return hashMapper.toEntities(numbers.stream().map(Base62::base62).toList());
    }


}

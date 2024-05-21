package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.Base62EncoderConfig;
import org.springframework.stereotype.Component;



@Component
public class Base62Encoder extends BaseEncoder {

    public Base62Encoder(){
        super(Base62EncoderConfig.ENCODING_FACTOR, Base62EncoderConfig.BASE_62_ALPHABET);
    }
}

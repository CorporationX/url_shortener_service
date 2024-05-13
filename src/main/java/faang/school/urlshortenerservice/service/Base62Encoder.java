package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;



@Component
public class Base62Encoder extends BaseEncoder {

    public Base62Encoder(){
        super(62, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }
}

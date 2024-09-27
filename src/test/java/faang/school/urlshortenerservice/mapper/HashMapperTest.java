package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class HashMapperTest {

    private final HashMapper hashMapper = Mappers.getMapper(HashMapper.class);

    private Hash hashEntity;
    private HashDto hashDto;

    private String hash;

    @BeforeEach
    void setUp() {
        hash = "https://ex.com/sg2c4";

        hashEntity = Hash.builder()
                .hash(hash)
                .build();

        hashDto = HashDto.builder()
                .hash(hash)
                .build();
    }

    @Test
    public void testEntityToDto() {
        HashDto result = hashMapper.toDto(hashEntity);

        assertEquals(hash, result.getHash());
    }

    @Test
    public void testDtoToEntity() {
        Hash result = hashMapper.toEntity(hashDto);

        assertEquals(hash, result.getHash());
    }
}
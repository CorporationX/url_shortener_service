package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.localcache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlService urlService;
    @Captor
    private ArgumentCaptor<UrlAssociation> urlAssociationArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    private Hash hash = new Hash("12345");

    private OriginalUrlDto originalUrlDto;
    private UrlAssociation urlAssociation = new UrlAssociation();

    @BeforeEach
    void init() {
        urlAssociation.setUrl("https://faangschol.com/abcxyz123456789");
        urlAssociation.setHash(hash.getHash());
        originalUrlDto = new OriginalUrlDto(urlAssociation.getUrl());

    }


    @Test
    public void saveUrlAssociationTest() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any())).thenReturn(urlAssociation);
        doNothing().when(urlCacheRepository).saveUrl(any(), any());

        Hash receivedHash = urlService.saveUrlAssociation(originalUrlDto);

        verify(hashCache, only()).getHash();
        verify(urlRepository, only()).save(urlAssociationArgumentCaptor.capture());
        UrlAssociation capturedValue = urlAssociationArgumentCaptor.getValue();
        assertEquals(originalUrlDto.getUrl(), capturedValue.getUrl());
        assertEquals(hash.getHash(), capturedValue.getHash());
        assertEquals(hash, receivedHash);

    }


    @Test
    public void getUrlByHashSuccessfulTest() {

        when(urlRepository.findById(hash.getHash()))
                .thenReturn(Optional.of(new UrlAssociation(hash.getHash(), urlAssociation.getUrl(), LocalDateTime.now())));

        String result = urlService.getUrlByHash(hash.getHash());

        assertEquals(urlAssociation.getUrl(), result);
        verify(urlRepository, only()).findById(stringArgumentCaptor.capture());
        String capturedValue = stringArgumentCaptor.getValue();
        assertEquals(hash.getHash(), capturedValue);

    }

    @Test
    public void getUrlByHashUnsuccessfulTest() {
        when(urlRepository.findById(hash.getHash()))
                .thenReturn(Optional.empty());

        String result = urlService.getUrlByHash(hash.getHash());

        assertNull(result);

    }

}

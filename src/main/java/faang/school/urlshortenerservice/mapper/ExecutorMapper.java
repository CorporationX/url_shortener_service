package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.ThreadPoolPropsDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Mapper(componentModel = "spring")
public interface ExecutorMapper {
    void updateExecutor(ThreadPoolPropsDto propsDto, @MappingTarget ThreadPoolTaskExecutor executor);
}
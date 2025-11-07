package mapper;

import dto.EndpointHitDto;
import model.EndpointHit;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EndpointHitMapper {

    EndpointHitDto toDto(EndpointHit endpointHit);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    EndpointHit toEntity(EndpointHitDto endpointHitDto);
}

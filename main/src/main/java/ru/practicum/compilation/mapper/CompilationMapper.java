package ru.practicum.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.EventMapperUtil;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {EventMapperUtil.class, EventMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toEntity(NewCompilationDto newCompilationDto);


    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    void patch(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);
}

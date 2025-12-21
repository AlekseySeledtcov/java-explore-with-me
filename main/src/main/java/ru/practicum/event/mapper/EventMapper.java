package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.model.Location;
import ru.practicum.participationRequest.dto.UpdateEventAdminRequest;
import ru.practicum.participationRequest.dto.UpdateEventUserRequest;
import ru.practicum.participationRequest.service.RequestService;
import ru.practicum.user.UserMapper;
import ru.practicum.user.model.User;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CategoryMapper.class, UserMapper.class, RequestService.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(Event event);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventDate", source = "eventDto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "participantLimit", source = "eventDto.participantLimit", defaultExpression = "java(0)")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "requestModeration", source = "eventDto.requestModeration", defaultExpression = "java(true)")
    @Mapping(target = "state", ignore = true)
    Event toEntity(NewEventDto eventDto, Category category, User initiator, Location location);


    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "views", ignore = true)
    EventFullDto toFullDto(Event event);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "eventDate", source = "updateRequest.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    void patchAdminRequest(@MappingTarget Event event, UpdateEventAdminRequest updateRequest, Category category, Location location);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "eventDate", source = "updateRequest.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    void patchUserRequest(@MappingTarget Event event, UpdateEventUserRequest updateRequest, Category category, Location location);
}

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

    @Named("toShortDto")
    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "category", qualifiedByName = {"CategoryToDto"})
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "event.initiator", qualifiedByName = "UserToShortDto")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(Event event);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventDate", source = "eventDto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "paid", source = "eventDto.paid", defaultExpression = "java(false)")
    @Mapping(target = "participantLimit", source = "eventDto.participantLimit", defaultExpression = "java(0)")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "requestModeration", source = "eventDto.requestModeration", defaultExpression = "java(true)")
    @Mapping(target = "state", ignore = true)
    Event toEntity(NewEventDto eventDto, Category category, User initiator, Location location);


    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "event.category", qualifiedByName = "CategoryToDto")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests", defaultExpression = "java(0L)")
    @Mapping(target = "createdOn", source = "event.createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "event.initiator", qualifiedByName = "UserToShortDto")
    @Mapping(target = "location", source = "event.location")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "participantLimit", source = "event.participantLimit")
    @Mapping(target = "publishedOn", source = "event.publishedOn")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    @Mapping(target = "state", source = "event.state")
    @Mapping(target = "title", source = "event.title")
    EventFullDto toFullDto(Event event, Long confirmedRequests, Long views);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "annotation", source = "updateRequest.annotation")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "description", source = "updateRequest.description")
    @Mapping(target = "eventDate", source = "updateRequest.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "paid", source = "updateRequest.paid")
    @Mapping(target = "participantLimit", source = "updateRequest.participantLimit")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "requestModeration", source = "updateRequest.requestModeration")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "title", source = "updateRequest.title")
    void patchAdminRequest(@MappingTarget Event event, UpdateEventAdminRequest updateRequest, Category category, Location location);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "annotation", source = "updateRequest.annotation")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "description", source = "updateRequest.description")
    @Mapping(target = "eventDate", source = "updateRequest.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "paid", source = "updateRequest.paid")
    @Mapping(target = "participantLimit", source = "updateRequest.participantLimit")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "requestModeration", source = "updateRequest.requestModeration")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "title", source = "updateRequest.title")
    void patchUserRequest(@MappingTarget Event event, UpdateEventUserRequest updateRequest, Category category, Location location);
}

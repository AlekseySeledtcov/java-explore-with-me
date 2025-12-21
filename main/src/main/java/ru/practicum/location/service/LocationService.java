package ru.practicum.location.service;

import ru.practicum.location.model.Location;

public interface LocationService {

    Location saveLocation(Location location);

    Location getLocation(Long id);

}

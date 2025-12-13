package ru.practicum.location.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

// Широта и долгота места проведения события
public class LocationDto {

    // Широта: от -90.0 до +90.0
    @Column(nullable = false, precision = 9, scale = 6)
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double lat;

    // Долгота: от -180.0 до +180.0
    @Column(nullable = false, precision = 10, scale = 6)
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double lon;
}

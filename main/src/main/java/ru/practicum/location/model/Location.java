package ru.practicum.location.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location {

    // Идентификатор
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Широта
    @Column(nullable = false)
    private Double lat;

    // Долгота
    @Column(nullable = false)
    private Double lon;
}

package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.sevice.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilationsPage(
            @RequestParam(name = "pinned", defaultValue = "false") boolean pinned,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") int from,
            @Min(1) @RequestParam(value = "size", defaultValue = "10") int size) {

        log.debug("Получение подборок событий");
        return ResponseEntity
                .status((HttpStatus.OK))
                .body(compilationService.getCompilationsPage(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(
            @Positive @PathVariable(value = "compId") Long id) {

        log.debug("Полчение подборки событий по его id={}", id);
        CompilationDto result = compilationService.getCompilationById(id);
        return ResponseEntity.ok().body(result);
    }
}

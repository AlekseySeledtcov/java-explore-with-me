package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.sevice.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto createCompilation(
            @Valid @RequestBody NewCompilationDto newCompilationDto) {

        log.debug("Добавление новой подборки.\nТело запроса: {}", newCompilationDto);

        return compilationService.createCompilations(newCompilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void deleteCompilation(@Positive @PathVariable(value = "compId") Long id) {

        log.debug("Удаление подборки по id={}", id);

        compilationService.deleteCompilation(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
            @Positive @PathVariable(value = "compId") Long id) {

        log.debug("Обновить информацию о подборке по id={}\n. Тело запроса:{}", id, updateCompilationRequest);

        return compilationService.patchCompilations(updateCompilationRequest, id);
    }
}

package ru.practicum.category.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CategoryDto> getCategories(
            @Min(0) @RequestParam(name = "from", defaultValue = "0") int from,
            @Min(1) @RequestParam(name = "size", defaultValue = "10") int size) {

        log.debug("Запрос списка категорий с пагинацией");

        return categoryService.getCategories(from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(
            @Positive @PathVariable(name = "catId") Long id) {

        log.debug("Запрос категории по ID {}", id);

        return categoryService.getCategoryById(id);
    }
}

package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto postCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        log.debug("Добавление новой категории. Тело запроса {}", newCategoryDto);

        return categoryService.postCategory(newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void deleteCategory(
            @Positive @Min(1) @PathVariable(name = "catId") Long id) {

        log.debug("Удаление категории с id={}", id);

        categoryService.deleteCategory(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            @PathVariable(name = "catId") Long id) {

        log.debug("Изменение категории \n" +
                "Тело запроса {}\n" +
                "Идентификатор id={}\n", categoryDto, id);

        return categoryService.patchCategory(categoryDto, id);
    }
}

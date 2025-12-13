package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<CategoryDto> postCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        log.debug("Добавление новой категории. Тело запроса {}", newCategoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.postCategory(newCategoryDto));
    }


    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(
            @Positive @Min(1) @PathVariable(name = "catId") Long id) {

        log.debug("Удаление категории с id={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> patchCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            @PathVariable(name = "catId") Long id) {

        log.debug("Изменение категории \n" +
                "Тело запроса {}\n" +
                "Идентификатор id={}\n", categoryDto, id);
        CategoryDto updated = categoryService.patchCategory(categoryDto, id);
        return ResponseEntity.ok(updated);
    }
}

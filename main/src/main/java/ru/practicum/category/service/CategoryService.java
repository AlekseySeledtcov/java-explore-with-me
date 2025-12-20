package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto postCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto patchCategory(CategoryDto categoryDto, Long id);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long id);

    Category getCategoryEntityByIdOrThrow(Long categoryId);
}

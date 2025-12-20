package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.service.EventCommonService;
import ru.practicum.exceptions.AlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.utils.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventCommonService eventCommonService;

    @Override
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        if (existsByName(newCategoryDto.getName())) {
            throw new AlreadyExistsException("Категория с таким названием " + newCategoryDto.getName() + "уже существует");
        }
        Category category = categoryRepository.save(categoryMapper.toEntity(newCategoryDto));
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!existsById(id)) throw new NotFoundException("Category with id=" + id + " was not found");
        if (eventCommonService.existsEventByCategoryId(id))
            throw new AlreadyExistsException("The category is not empty");
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto patchCategory(CategoryDto categoryDto, Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Категория с id " + id + "не найдена"));

        if (!categoryDto.getName().equals(category.getName()) &&
                existsByName(categoryDto.getName())) {
            throw new AlreadyExistsException(
                    "Категория с таким именем " + categoryDto.getName() + "уже существует");
        }

        categoryMapper.patch(categoryDto, category);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }


    @Transactional(readOnly = true)
    @Override
    public Category getCategoryEntityByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категории с указанным id " + categoryId + "не существует"));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.getContent().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category result = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The required object was not found."));
        return categoryMapper.toDto(result);
    }

    private boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    private boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}

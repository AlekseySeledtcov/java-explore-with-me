package ru.practicum.compilation.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.utils.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toEntity(newCompilationDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(savedCompilation);
    }


    @Transactional
    @Override
    public void deleteCompilation(Long id) {
        if (!existsById(id)) throw new NotFoundException("Compilation with " + id + " was not found");
        compilationRepository.deleteById(id);
    }


    @Transactional
    @Override
    public CompilationDto patchCompilations(UpdateCompilationRequest updateCompilationRequest, long id) {
        Compilation oldCompilation = findByIdOrThrow(id);

        compilationMapper.patch(updateCompilationRequest, oldCompilation);
        Compilation result = compilationRepository.save(oldCompilation);

        return compilationMapper.toDto(result);
    }


    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilationsPage(boolean pinned, int from, int size) {
        Pageable pageable = PaginationUtils.createPageable(from, size, null);

        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = findByIdOrThrow(id);

        return compilationMapper.toDto(compilation);
    }

    private boolean existsById(long id) {
        return compilationRepository.existsById(id);
    }

    private Compilation findByIdOrThrow(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + "was not found"));
    }
}

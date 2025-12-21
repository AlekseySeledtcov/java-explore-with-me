package ru.practicum.compilation.sevice;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilations(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long id);

    CompilationDto patchCompilations(UpdateCompilationRequest updateCompilationRequest, long id);

    List<CompilationDto> getCompilationsPage(boolean pinned, int from, int size);

    CompilationDto getCompilationById(long id);
}

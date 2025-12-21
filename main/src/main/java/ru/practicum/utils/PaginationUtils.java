package ru.practicum.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    private static final int MAX_PAGE_SIZE = 100;

    public static Pageable createPageable(int from, int size, String sort) {
        int offset = normalizeOffset(from);
        int limit = normalizeLimit(size);
        int page = offset / limit;

        String sortField = (sort != null && !sort.isEmpty()) ? sort : "id";

        return PageRequest.of(page, limit, Sort.by(sortField).ascending());
    }

    private static int normalizeOffset(Integer from) {
        return from != null ? Math.max(0, from) : 0;
    }

    private static int normalizeLimit(Integer size) {
        return Math.max(1, Math.min(size, MAX_PAGE_SIZE));
    }
}

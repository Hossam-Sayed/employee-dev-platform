package com.edp.library.utils;

import com.edp.library.model.PaginationResponseDTO;
import com.edp.library.model.PaginationRequestDTO;
import com.edp.library.data.entity.blog.BlogSubmission;
import com.edp.library.data.entity.wiki.WikiSubmission;
import com.edp.library.data.entity.learning.LearningSubmission;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class PaginationUtils {

    /**
     * Maps the user-provided sortBy to the actual field name for a given entity.
     */
    public String getActualSortBy(String requestedSortBy, Class<?> entityClass) {
        if ("createdAt".equalsIgnoreCase(requestedSortBy)) {
            if (BlogSubmission.class.isAssignableFrom(entityClass)
                    || WikiSubmission.class.isAssignableFrom(entityClass)
                    || LearningSubmission.class.isAssignableFrom(entityClass)) {
                return "submittedAt";
            }
            return "createdAt";
        }

        // Fallback: return the requested field as-is
        return requestedSortBy;
    }

    /**
     * Creates a Pageable instance using the given PaginationRequestDTO and entity class.
     */
    public Pageable toPageable(PaginationRequestDTO paginationRequestDTO, Class<?> entityClass) {
        String actualSortBy = getActualSortBy(paginationRequestDTO.getSortBy(), entityClass);
        Sort.Direction direction = Objects.requireNonNullElse(paginationRequestDTO.getSortDirection(), Sort.Direction.DESC);
        return PageRequest.of(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getSize(),
                Sort.by(direction, actualSortBy)
        );
    }

    /**
     * Maps a Page<T> and its converted content to a PaginationResponseDTO<U>.
     */
    public <T, U> PaginationResponseDTO<U> mapToPaginationResponseDTO(Page<T> page, List<U> content) {
        return PaginationResponseDTO.<U>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}

package com.edp.library.data.repository.tag;

import com.edp.library.data.entity.tag.TagRequest;
import com.edp.library.data.enums.TagRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRequestRepository extends JpaRepository<TagRequest, Long> {

    // Find all tag requests by a specific requester
    List<TagRequest> findByRequesterId(Long requesterId);

    Page<TagRequest> findByRequesterId(Long requesterId, Pageable pageable);

    // Find all tag requests by status (for admin review dashboard)
    List<TagRequest> findByStatus(TagRequestStatus status);

    Page<TagRequest> findByStatus(TagRequestStatus status, Pageable pageable);

    // Find a tag request by its name and status (to prevent duplicate pending requests)
    Optional<TagRequest> findByRequestedNameIgnoreCaseAndStatus(String requestedName, TagRequestStatus status);

    // Find a tag request by its name and status (to prevent duplicate requests)
    Optional<TagRequest> findByRequestedNameIgnoreCase(String tagName);
}
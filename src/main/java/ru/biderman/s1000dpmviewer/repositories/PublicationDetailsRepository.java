package ru.biderman.s1000dpmviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;

import java.util.List;
import java.util.Optional;

public interface PublicationDetailsRepository extends JpaRepository<PublicationDetails, Long> {
    boolean existsByCodeAndIssueAndLanguage(String code, String issue, String language);

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<PublicationDetails> findAll();

    @PostAuthorize("hasPermission(returnObject.orElse(null), 'READ')")
    Optional<PublicationDetails> findById(Long id);
}

package ru.biderman.s1000dpmviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;

public interface PublicationDetailsRepository extends JpaRepository<PublicationDetails, Long> {
    boolean existsByCodeAndIssueAndLanguage(String code, String issue, String language);
}

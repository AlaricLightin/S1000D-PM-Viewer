package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPublicationException;
import ru.biderman.s1000dpmviewer.exceptions.PublicationAlreadyExistsException;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.repositories.PublicationDetailsRepository;
import ru.biderman.s1000dpmviewer.repositories.PublicationRepository;
import ru.biderman.s1000dpmviewer.xmlparsers.PublicationParser;

import javax.transaction.Transactional;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationDetailsRepository detailsRepository;
    private final PublicationParser parser;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional
    public Publication add(InputStream inputStream) throws InvalidPublicationException, PublicationAlreadyExistsException {
        Publication newPublication = parser.createPublication(inputStream);
        PublicationDetails details = newPublication.getDetails();
        if (!detailsRepository.existsByCodeAndIssueAndLanguage(details.getCode(), details.getIssue(), details.getLanguage())) {
            Publication savedPublication = publicationRepository.save(newPublication);
            authorizationService.createAdminRights(savedPublication.getId());
            return savedPublication;
        }
        else
            throw new PublicationAlreadyExistsException();
    }

    @Override
    @Transactional
    public void deleteById(long id) throws PublicationNotFoundException {
        if (publicationRepository.existsById(id))
            publicationRepository.deleteById(id);
        else
            throw new PublicationNotFoundException();
    }

    @Override
    public Publication findById(long id) throws PublicationNotFoundException{
        return publicationRepository.findById(id)
                .orElseThrow(PublicationNotFoundException::new);
    }

    @Override
    public Entry getContentById(long id) throws PublicationNotFoundException {
        return publicationRepository.findById(id)
                .map(publication -> parser.getPublicationContent(
                        publication.getDocument()))
                .orElseThrow(PublicationNotFoundException::new);
    }
}

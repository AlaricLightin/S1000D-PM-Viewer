package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.repositories.PublicationRepository;
import ru.biderman.s1000dpmviewer.xmlparsers.PublicationParser;

import javax.transaction.Transactional;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationParser parser;

    @Override
    // TODO сделать обработку исключений, когда публикация не создалась или когда такая уже есть
    public Publication add(InputStream inputStream) {
        Publication newPublication = parser.createPublication(inputStream);
        return publicationRepository.save(newPublication);
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
}

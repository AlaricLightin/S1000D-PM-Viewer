package ru.biderman.s1000dpmviewer.services;

import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;

import java.io.InputStream;

public interface PublicationService {
    Publication add(InputStream inputStream);
    void deleteById(long id) throws PublicationNotFoundException;
    Publication findById(long id) throws PublicationNotFoundException;
}

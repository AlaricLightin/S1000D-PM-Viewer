package ru.biderman.s1000dpmviewer.xmlparsers;

import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPublicationException;

import java.io.InputStream;

public interface PublicationParser {
    Publication createPublication(InputStream inputStream) throws InvalidPublicationException;
    Entry getPublicationContent(Document document);
}

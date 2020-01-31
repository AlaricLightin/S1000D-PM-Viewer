package ru.biderman.s1000dpmviewer.xmlparsers;

import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;

import java.io.InputStream;

public interface PublicationParser {
    Publication createPublication(InputStream inputStream);
    PublicationDetails getPublicationDetails(Document document);
    Entry getPublicationContent(Document document);
}

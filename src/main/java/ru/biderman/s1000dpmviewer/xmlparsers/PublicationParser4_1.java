package ru.biderman.s1000dpmviewer.xmlparsers;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Ident;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPublicationException;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;
import ru.biderman.s1000dpmviewer.utils.XMLParseUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
public class PublicationParser4_1 implements PublicationParser{
    @Override
    public PublicationDetails getPublicationDetails(Document document) throws InvalidPublicationException {
        PublicationDetails result = new PublicationDetails();

        Element pmAddress = XMLParseUtils.getElement(document, "//identAndStatusSection/pmAddress")
                .orElseThrow(InvalidPublicationException::new);

        Element pmIdent = XMLParseUtils.getFirstChildElement(pmAddress, "pmIdent")
                .orElseThrow(InvalidPublicationException::new);
        Ident ident = IdentParser4_1.createPMIdent(pmIdent);
        if (!ident.isValidVersion())
            throw new InvalidPublicationException();
        result.setIdent(ident);

        Element pmAddressItems = XMLParseUtils.getFirstChildElement(pmAddress, "pmAddressItems")
                .orElseThrow(InvalidPublicationException::new);

        LocalDate issueDate = XMLParseUtils.getDate(pmAddressItems, "issueDate")
                .orElseThrow(InvalidPublicationException::new);
        result.setIssueDate(issueDate);

        Element pmTitle = XMLParseUtils.getFirstChildElement(pmAddressItems, "pmTitle")
                .orElseThrow(InvalidPublicationException::new);
        result.setTitle(pmTitle.getTextContent());

        return result;
    }

    @Override
    public Publication createPublication(InputStream inputStream) throws InvalidPublicationException{
        Document document = XMLDocumentUtils.getDocumentFromStream(inputStream);
        if (document == null)
            throw new InvalidPublicationException();
        Publication result = new Publication();
        result.setXml(XMLDocumentUtils.getStringFromDocument(document));
        PublicationDetails details = getPublicationDetails(document);
        details.setLoadDateTime(ZonedDateTime.now());
        result.setDetails(details);
        details.setPublication(result);
        return result;
    }

    @Override
    public Entry getPublicationContent(Document document) {
        return PublicationContentParser4_1.createRootEntry(document);
    }
}

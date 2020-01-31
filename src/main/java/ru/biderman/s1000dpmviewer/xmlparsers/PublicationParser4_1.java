package ru.biderman.s1000dpmviewer.xmlparsers;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;
import ru.biderman.s1000dpmviewer.utils.XMLParseUtils;

import java.io.InputStream;
import java.time.ZonedDateTime;

@Service
public class PublicationParser4_1 implements PublicationParser{
    @Override
    // TODO обработка ошибок, если каких-то элементов нет
    public PublicationDetails getPublicationDetails(Document document) {
        PublicationDetails result = new PublicationDetails();

        Element pmAddress = XMLParseUtils.getElement(document, "//identAndStatusSection/pmAddress");
        if (pmAddress != null) {
            Element pmIdent = XMLParseUtils.getFirstChildElement(pmAddress, "pmIdent");
            if (pmIdent != null) {
                result.setIdent(IdentParser4_1.createPMIdent(pmIdent));
            }

            Element pmAddressItems = XMLParseUtils.getFirstChildElement(pmAddress, "pmAddressItems");
            if (pmAddressItems != null) {
                result.setIssueDate(XMLParseUtils.getDate(pmAddressItems, "issueDate"));
                Element pmTitle = XMLParseUtils.getFirstChildElement(pmAddressItems, "pmTitle");
                if (pmTitle != null)
                    result.setTitle(pmTitle.getTextContent());
            }
        }

        return result;
    }

    @Override
    public Publication createPublication(InputStream inputStream) {
        Document document = XMLDocumentUtils.getDocumentFromStream(inputStream);
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

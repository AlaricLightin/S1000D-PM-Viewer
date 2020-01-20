package ru.biderman.s1000dpmviewer.xmlparsers;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;
import ru.biderman.s1000dpmviewer.utils.XMLParseUtils;

import java.io.InputStream;

@Service
public class PublicationParser4_1 implements PublicationParser{
    @Override
    public PublicationDetails getPublicationDetails(Document document) {
        PublicationDetails result = new PublicationDetails();

        Element pmAddress = XMLParseUtils.getElement(document, "//identAndStatusSection/pmAddress");
        if (pmAddress != null) {
            Element pmIdent = XMLParseUtils.getFirstChildElement(pmAddress, "pmIdent");
            if (pmIdent != null) {
                result.setCode(XMLParseUtils.getDelimitedTextFromAttrs(pmIdent, "pmCode",
                        "modelIdentCode", "pmIssuer", "pmNumber", "pmVolume"));
                result.setIssue(XMLParseUtils.getDelimitedTextFromAttrs(pmIdent, "issueInfo",
                        "issueNumber", "inWork"));
                result.setLanguage(XMLParseUtils.getDelimitedTextFromAttrs(pmIdent, "language",
                        "languageIsoCode", "countryIsoCode"));
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
        result.setDetails(details);
        details.setPublication(result);
        return result;
    }
}

package ru.biderman.s1000dpmviewer.xmlparsers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.DMRef;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.EntryElement;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Ident;
import ru.biderman.s1000dpmviewer.utils.XMLParseUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

class PublicationContentParser4_1 {
    static Entry createRootEntry(Document document) {
        String title = XMLParseUtils.getTextFromElement(document, "//identAndStatusSection/pmAddress/pmAddressItems/pmTitle");

        HashMap<String, Function<Element, EntryElement>> functionMap = new HashMap<>();
        functionMap.put("pmEntry", PublicationContentParser4_1::createPMEntry);

        List<EntryElement> list = XMLParseUtils.getElement(document, "//content")
                .map(content -> XMLParseUtils.getChildList(content, functionMap))
                .orElse(Collections.emptyList());

        return new Entry(title, list);
    }

    static EntryElement createPMEntry(Element element) {
        String title = XMLParseUtils.getTextFromChildElement(element, "pmEntryTitle");

        HashMap<String, Function<Element, EntryElement>> functionMap = new HashMap<>();
        functionMap.put("pmEntry", PublicationContentParser4_1::createPMEntry);
        functionMap.put("dmRef", PublicationContentParser4_1::createDmRef);
        // TODO добавить pmRef?
        List<EntryElement> list = XMLParseUtils.getChildList(element, functionMap);

        return new Entry(title, list);
    }

    static DMRef createDmRef(Element dmRef) {
        Element dmRefIdent = XMLParseUtils.getFirstChildElement(dmRef, "dmRefIdent").orElse(null);
        if (dmRefIdent == null)
            return null;

        Ident ident = IdentParser4_1.createDMIdent(dmRefIdent);
        String title = XMLParseUtils.getElement(dmRef, "dmRefAddressItems/dmTitle")
                .map(dmTitle -> {
                    String result = XMLParseUtils.getTextFromChildElement(dmTitle, "techName");
                    String infoName = XMLParseUtils.getTextFromChildElement(dmTitle, "infoName");
                    if (!result.isEmpty()) {
                        if (!infoName.isEmpty())
                            result = result + " - " + infoName;
                    }
                    else
                        result = infoName;

                    return result;
                })
                .orElse("");

        return new DMRef(ident, title);
    }
}

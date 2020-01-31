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
        Element content = XMLParseUtils.getElement(document, "//content");
        List<EntryElement> list;
        if (content != null) // TODO обработка ошибок
            list = XMLParseUtils.getChildList(content, functionMap);
        else
            list = Collections.emptyList();
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
        Element dmRefIdent = XMLParseUtils.getFirstChildElement(dmRef, "dmRefIdent");
        if (dmRefIdent == null)
            return null;

        Ident ident = IdentParser4_1.createDMIdent(dmRefIdent);
        String title = "";
        Element dmTitle = XMLParseUtils.getElement(dmRef, "dmRefAddressItems/dmTitle");
        if (dmTitle != null) {
            title = XMLParseUtils.getTextFromChildElement(dmTitle, "techName");
            String infoName = XMLParseUtils.getTextFromChildElement(dmTitle, "infoName");
            if (!title.isEmpty()) {
                if (!infoName.isEmpty())
                    title = title + " - " + infoName;
            }
            else
                title = infoName;
        }

        return new DMRef(ident, title);
    }
}

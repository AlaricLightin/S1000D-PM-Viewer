package ru.biderman.s1000dpmviewer.xmlparsers;

import org.w3c.dom.Element;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Ident;
import ru.biderman.s1000dpmviewer.utils.XMLParseUtils;

import java.util.function.Function;

class IdentParser4_1 {
    private static Ident createIdent(Element identElement, Function<Element, String> codeFunction) {
        assert identElement != null;
        String code = codeFunction.apply(identElement);
        String issue = XMLParseUtils.getDelimitedTextFromAttrs(identElement, "issueInfo",
                "issueNumber", "inWork").orElse(null);
        String language = XMLParseUtils.getDelimitedTextFromAttrs(identElement, "language",
                "languageIsoCode", "countryIsoCode").orElse(null);

        return new Ident(code, issue, language);
    }

    private static String getPmCode(Element identElement) {
        return XMLParseUtils.getDelimitedTextFromAttrs(identElement, "pmCode",
                "modelIdentCode", "pmIssuer", "pmNumber", "pmVolume").orElse("");
    }

    static Ident createPMIdent(Element pmIdent) {
        return createIdent(pmIdent, IdentParser4_1::getPmCode);
    }

    private static String getDMCode(Element dmIdent) {
        Element dmCodeElement = XMLParseUtils.getFirstChildElement(dmIdent, "dmCode").orElse(null);
        if (dmCodeElement == null)
            return "";

        String result = String.join("-",
                dmCodeElement.getAttribute("modelIdentCode"),
                dmCodeElement.getAttribute("systemDiffCode"),
                dmCodeElement.getAttribute("systemCode"),
                dmCodeElement.getAttribute("subSystemCode") + dmCodeElement.getAttribute("subSubSystemCode"),
                dmCodeElement.getAttribute("assyCode"),
                dmCodeElement.getAttribute("disassyCode") + dmCodeElement.getAttribute("disassyCodeVariant"),
                dmCodeElement.getAttribute("infoCode") + dmCodeElement.getAttribute("infoCodeVariant"),
                dmCodeElement.getAttribute("itemLocationCode"));

        String learnPart = dmCodeElement.getAttribute("learnCode");
        if (learnPart != null && !learnPart.isEmpty()) {
            String lEC = dmCodeElement.getAttribute("learnEventCode");
            if (lEC != null && !lEC.isEmpty())
                learnPart = learnPart + lEC;

            result = String.join("-", result, learnPart);
        }

        return result;
    }

    static Ident createDMIdent(Element dmIdent) {
        return createIdent(dmIdent, IdentParser4_1::getDMCode);
    }
}

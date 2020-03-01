package ru.biderman.s1000dpmviewer.domain.publicationcontent;

import lombok.Getter;

@Getter
public class DMRef extends EntryElement {
    private final String code;
    private final String issue;
    private final String language;
    private final String title;

    public DMRef(Ident ident, String title) {
        this.code = ident.getCode();
        this.issue = ident.getIssue();
        this.language = ident.getLanguage();
        this.title = title;
    }
}

package ru.biderman.s1000dpmviewer.domain.publicationcontent;

import java.util.Objects;

public class Ident {
    private final String code;
    private final String issue;
    private final String language;

    public Ident(String code, String issue, String language) {
        this.code = code != null ? code.toUpperCase() : null ;
        this.issue = issue;
        this.language = language != null ? language.toUpperCase() : null;
    }

    public String getCode() {
        return code;
    }

    public String getIssue() {
        return issue;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ident ident = (Ident) o;
        return Objects.equals(code, ident.code) &&
                Objects.equals(issue, ident.issue) &&
                Objects.equals(language, ident.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, issue, language);
    }
}

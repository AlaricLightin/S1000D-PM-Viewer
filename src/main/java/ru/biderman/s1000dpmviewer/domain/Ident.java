package ru.biderman.s1000dpmviewer.domain;

import java.util.Objects;

public class Ident {
    private final String code;
    private final String issue;
    private final String language;

    public Ident(String code, String issue, String language) {
        assert code != null && issue != null && language != null;
        this.code = code;
        this.issue = issue;
        this.language = language;
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
        return Objects.equals(code.toUpperCase(), ident.code.toUpperCase()) &&
                Objects.equals(issue.toUpperCase(), ident.issue.toUpperCase()) &&
                Objects.equals(language.toUpperCase(), ident.language.toUpperCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.toUpperCase(), issue.toUpperCase(), language.toUpperCase());
    }
}

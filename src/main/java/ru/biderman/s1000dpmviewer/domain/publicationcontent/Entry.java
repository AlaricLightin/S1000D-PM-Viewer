package ru.biderman.s1000dpmviewer.domain.publicationcontent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Entry extends EntryElement {
    private final String title;
    private final List<EntryElement> children;
}

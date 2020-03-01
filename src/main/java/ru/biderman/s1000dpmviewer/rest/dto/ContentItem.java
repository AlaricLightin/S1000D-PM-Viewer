package ru.biderman.s1000dpmviewer.rest.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ContentItem {
    private final String name;
    private final List<ContentItem> children;
}

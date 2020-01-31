package ru.biderman.s1000dpmviewer.rest;

import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.DMRef;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.rest.dto.ContentItem;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
class ContentDtoService {
    static String getDmNameString(DMRef dmRef) {
        String code = dmRef.getCode();
        if (!dmRef.getIssue().isEmpty() || !dmRef.getLanguage().isEmpty())
            code = String.join(", ", code, dmRef.getIssue(), dmRef.getLanguage());

        return String.format("[%s] %s", code, dmRef.getTitle());
    }

    List<ContentItem> createContentItems(Entry contentRootEntry) {
        return Collections.singletonList(createEntryContent(contentRootEntry));
    }

    private ContentItem createEntryContent(Entry entry) {
        String name = entry.getTitle();
        if(name.isEmpty())
            name = "Entry with empty title";

        List<ContentItem> children = entry.getChildren().stream()
                .map(entryElement -> {
                    if (entryElement instanceof Entry)
                        return createEntryContent((Entry) entryElement);
                    else if (entryElement instanceof DMRef)
                        return createDMRefContent((DMRef) entryElement);
                    else
                        return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new ContentItem(name, children);
    }

    private ContentItem createDMRefContent(DMRef dmRef) {
        return new ContentItem(getDmNameString(dmRef), Collections.emptyList());
    }
}

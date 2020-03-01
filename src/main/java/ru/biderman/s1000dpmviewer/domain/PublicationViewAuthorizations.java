package ru.biderman.s1000dpmviewer.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PublicationViewAuthorizations {
    private boolean permitAll;
    private List<String> usernameList;

    private PublicationViewAuthorizations(boolean permitAll, List<String> usernameList) {
        this.permitAll = permitAll;
        this.usernameList = usernameList;
    }

    public static PublicationViewAuthorizations createForAll() {
        return new PublicationViewAuthorizations(true, Collections.emptyList());
    }

    public static PublicationViewAuthorizations createForUsers(List<String> usernameList) {
        return new PublicationViewAuthorizations(false, usernameList);
    }
}

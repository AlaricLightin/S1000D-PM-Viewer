package ru.biderman.s1000dpmviewer.services;

import ru.biderman.s1000dpmviewer.domain.PublicationViewAuthorizations;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;

public interface AuthorizationService {
    PublicationViewAuthorizations getViewAuthorizations(long publicationId) throws PublicationNotFoundException;
    void setViewAuthorizations(long publicationId, PublicationViewAuthorizations viewAuthorizations) throws PublicationNotFoundException;
    void createAdminRights(long publicationId);
}

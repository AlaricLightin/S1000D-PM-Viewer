package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.PublicationViewAuthorizations;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final MutableAclService mutableAclService;

    private static final Sid ROLE_ADMIN = new GrantedAuthoritySid("ROLE_ADMIN");
    private static final Sid ROLE_EDITOR = new GrantedAuthoritySid("ROLE_EDITOR");
    private static final Sid ROLE_USER = new GrantedAuthoritySid("ROLE_USER");
    private static final Sid ROLE_ANONYMOUS = new GrantedAuthoritySid("ROLE_ANONYMOUS");

    @Override
    public PublicationViewAuthorizations getViewAuthorizations(long publicationId) throws PublicationNotFoundException {
        ObjectIdentity publicationIdentity = new ObjectIdentityImpl(Publication.class, publicationId);
        Acl publicationAcl;
        try {
            publicationAcl = mutableAclService.readAclById(publicationIdentity);
        } catch (NotFoundException e) {
            throw new PublicationNotFoundException();
        }

        boolean permitAll = false;
        List<String> usernameList = new ArrayList<>();
        List<AccessControlEntry> entries = publicationAcl.getEntries();
        for(int i = 1; i < entries.size(); i++) {
            AccessControlEntry ace = entries.get(i);
            Sid sid = ace.getSid();
            if (sid.equals(ROLE_ANONYMOUS)) {
                permitAll = true;
                break;
            }

            if (sid instanceof PrincipalSid) {
                usernameList.add(((PrincipalSid) sid).getPrincipal());
            }
        }

        return permitAll ? PublicationViewAuthorizations.createForAll()
                : PublicationViewAuthorizations.createForUsers(usernameList);
    }

    private MutableAcl getAndClearAcl(long publicationId) throws PublicationNotFoundException {
        ObjectIdentity publicationIdentity = new ObjectIdentityImpl(Publication.class, publicationId);
        MutableAcl publicationAcl;
        try {
            publicationAcl = (MutableAcl) mutableAclService.readAclById(publicationIdentity);
        } catch (NotFoundException e) {
            throw new PublicationNotFoundException();
        }

        // Удаляем все права, кроме первого (там разрешение на чтение для админов)
        for(int i = publicationAcl.getEntries().size() - 1; i > 0; i--) {
            publicationAcl.deleteAce(i);
        }

        return publicationAcl;
    }

    @Override
    @Transactional
    public void setViewAuthorizations(long publicationId, PublicationViewAuthorizations viewAuthorizations)
            throws PublicationNotFoundException {
        MutableAcl publicationAcl = getAndClearAcl(publicationId);
        if (publicationAcl == null)
            return;

        if(viewAuthorizations.isPermitAll()) {
            publicationAcl.insertAce(1, BasePermission.READ, ROLE_EDITOR, true);
            publicationAcl.insertAce(1, BasePermission.READ, ROLE_USER, true);
            publicationAcl.insertAce(1, BasePermission.READ, ROLE_ANONYMOUS, true);
        }
        else {
            viewAuthorizations.getUsernameList().forEach(username -> {
                Sid sid = new PrincipalSid(username);
                publicationAcl.insertAce(publicationAcl.getEntries().size(), BasePermission.READ, sid, true);
            });
        }

        mutableAclService.updateAcl(publicationAcl);
    }

    @Override
    public void createAdminRights(long publicationId) {
        ObjectIdentity publicationIdentity = new ObjectIdentityImpl(Publication.class, publicationId);
        ObjectIdentity detailsIdentity = new ObjectIdentityImpl(PublicationDetails.class, publicationId);

        Sid owner = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());

        MutableAcl publicationAcl = mutableAclService.createAcl(publicationIdentity);
        publicationAcl.setOwner(owner);
        mutableAclService.updateAcl(publicationAcl);

        publicationAcl.insertAce(0, BasePermission.READ, ROLE_ADMIN, true);
        publicationAcl.insertAce(1, BasePermission.READ, owner, true);
        mutableAclService.updateAcl(publicationAcl);

        MutableAcl detailsAcl = mutableAclService.createAcl(detailsIdentity);
        detailsAcl.setOwner(owner);
        detailsAcl.setParent(publicationAcl);
        detailsAcl.setEntriesInheriting(true);
        mutableAclService.updateAcl(detailsAcl);
    }
}

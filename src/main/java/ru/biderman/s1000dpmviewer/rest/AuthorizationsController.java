package ru.biderman.s1000dpmviewer.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.biderman.s1000dpmviewer.domain.PublicationViewAuthorizations;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.services.AuthorizationService;

@RestController
@RequiredArgsConstructor
public class AuthorizationsController {
    private final AuthorizationService authorizationService;

    @GetMapping("/publication/{id}/authorizations")
    public PublicationViewAuthorizations getViewAuthorizations(@PathVariable("id") long id) throws PublicationNotFoundException {
        return authorizationService.getViewAuthorizations(id);
    }

    @PutMapping("/publication/{id}/authorizations")
    public void setViewAuthorizations(@PathVariable("id") long id, @RequestBody PublicationViewAuthorizations authorizations)
            throws PublicationNotFoundException {
        authorizationService.setViewAuthorizations(id, authorizations);
    }

}

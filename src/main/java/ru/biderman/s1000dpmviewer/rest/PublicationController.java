package ru.biderman.s1000dpmviewer.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.services.PublicationDetailsService;
import ru.biderman.s1000dpmviewer.services.PublicationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicationController {
    private final PublicationService publicationService;
    private final PublicationDetailsService detailsService;

    @GetMapping("/publication")
    public List<PublicationDetails> getAllDetails() {
        return detailsService.findAll();
    }

    @PostMapping(value = "/publication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPublication(@RequestParam MultipartFile file, UriComponentsBuilder builder) throws IOException {
        Publication publication = publicationService.add(file.getInputStream());
        UriComponents uriComponents = builder.path("/publication/{id}").buildAndExpand(publication.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @DeleteMapping("/publication/{id}")
    public void deletePublication(@PathVariable("id") long id) throws PublicationNotFoundException {
        publicationService.deleteById(id);
    }

    @ExceptionHandler(PublicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void bookNotFoundHandler() {

    }
}
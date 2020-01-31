package ru.biderman.s1000dpmviewer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Ident;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "publication_details")
@Data
@NoArgsConstructor
@JsonIgnoreProperties("publication")
public class PublicationDetails {
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "issue", nullable = false)
    private String issue;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "load_datetime", nullable = false)
    private ZonedDateTime loadDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Publication publication;

    public void setIdent(Ident ident) {
        code = ident.getCode();
        issue = ident.getIssue();
        language = ident.getLanguage();
    }
}

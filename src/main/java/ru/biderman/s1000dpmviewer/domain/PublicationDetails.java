package ru.biderman.s1000dpmviewer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "publication_details")
@Data
@NoArgsConstructor
@JsonIgnoreProperties("publication")
public class PublicationDetails {
    @Id
    //@Column(name = "id", nullable = false)
    private long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "issue", nullable = false)
    private String issue;

    @Column(name = "language", nullable = false)
    private String language;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Publication publication;

    //TODO добавить дату разработки, дату загрузки. Что-то ещё?
}

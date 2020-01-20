package ru.biderman.s1000dpmviewer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "publications")
@Data
@NoArgsConstructor
public class Publication {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Type(type = "ru.biderman.s1000dpmviewer.domain.SQLXMLType")
    @Column(name = "xml_content", nullable = false)
    private String xml;

    @OneToOne(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PublicationDetails details;
}
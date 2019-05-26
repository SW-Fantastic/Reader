package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 书签
 */
@Entity
public class BookMark {

    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH)
    private Book markFor;

}

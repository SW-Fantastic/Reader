package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 目录
 */
@Entity
public class ContentsItem {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH)
    private Book located;

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String indexMode;

    @Override
    public String toString() {
        return title;
    }
}

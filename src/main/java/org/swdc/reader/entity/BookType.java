package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class BookType {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @OneToMany(mappedBy = "type",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<Book> books;

    @Override
    public String toString() {
        return this.getName();
    }

}

package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2019/5/23.
 */
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

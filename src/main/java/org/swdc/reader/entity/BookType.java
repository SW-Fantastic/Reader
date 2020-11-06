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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookType) {
            BookType otherOne = (BookType)obj;
            if (otherOne.getId() != null && this.getId() != null) {
                return otherOne.getId().equals(this.getId());
            } else if (this.getId() == null && otherOne.getId() == null && this.getName().equals(otherOne.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.getId() != null) {
            return this.getId().intValue();
        } else {
            return this.getName().hashCode();
        }
    }
}

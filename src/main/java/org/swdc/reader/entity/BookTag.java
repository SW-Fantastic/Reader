package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class BookTag {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "tags",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<Book> books;

    @Getter
    @Setter
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookTag) {
            BookTag tag = (BookTag) obj;
            if (this.getId() != null && tag.getId() != null) {
                return this.getId().equals(tag.getId());
            } else if (this.getId() == null && tag.getId() == null){
                return this.name.equals(tag.getName());
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

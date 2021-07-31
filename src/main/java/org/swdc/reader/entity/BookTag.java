package org.swdc.reader.entity;


import javax.persistence.*;
import java.util.Set;

@Entity
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Book> books;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

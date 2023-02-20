package org.swdc.reader.entity;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class BookType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "type",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    private Set<Book> books;

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

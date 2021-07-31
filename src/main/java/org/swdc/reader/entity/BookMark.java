package org.swdc.reader.entity;


import javax.persistence.*;

/**
 * 书签
 */
@Entity
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book markFor;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Book getMarkFor() {
        return markFor;
    }

    public void setMarkFor(Book markFor) {
        this.markFor = markFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

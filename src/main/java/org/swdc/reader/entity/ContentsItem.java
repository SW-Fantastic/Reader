package org.swdc.reader.entity;

import javax.persistence.*;

/**
 * 目录
 *
 *  在ReaderView中通过TOCAndFavoriteDialog操作，也可以使用Service直接控制。
 */
@Entity
public class ContentsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book located;

    private String location;

    private String title;

    @Override
    public String toString() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getLocated() {
        return located;
    }

    public void setLocated(Book located) {
        this.located = located;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

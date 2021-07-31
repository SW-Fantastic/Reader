package org.swdc.reader.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 书签表
     */
    @OneToMany(mappedBy = "markFor",cascade = CascadeType.REMOVE)
    private Set<BookMark> marks;

    /**
     * 目录
     */
    @OneToMany(mappedBy = "located",cascade = CascadeType.REMOVE)
    private Set<ContentsItem> contentsItems;

    /**
     * 分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private BookType type;

    /**
     * 标签
     */
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "books")
    private Set<BookTag> tags;


    /**
     * 标题
     */
    private String title;

    /**
     * sha值
     */
    private String shaCode;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private String size;


    /**
     * 文件的mime，用于读取文件
     */
    private String mimeData;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 作者
     */
    private String author;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShaCode() {
        return shaCode;
    }

    public void setShaCode(String shaCode) {
        this.shaCode = shaCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BookMark> getMarks() {
        return marks;
    }

    public void setMarks(Set<BookMark> marks) {
        this.marks = marks;
    }

    public Set<ContentsItem> getContentsItems() {
        return contentsItems;
    }

    public void setContentsItems(Set<ContentsItem> contentsItems) {
        this.contentsItems = contentsItems;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BookType getType() {
        return type;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public String getMimeData() {
        return mimeData;
    }

    public void setMimeData(String mimeData) {
        this.mimeData = mimeData;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<BookTag> getTags() {
        return tags;
    }

    public void setTags(Set<BookTag> tags) {
        this.tags = tags;
    }


}
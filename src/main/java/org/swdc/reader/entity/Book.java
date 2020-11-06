package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Book {

    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 标题
     */
    @Getter
    @Setter
    private String title;

    /**
     * sha值
     */
    @Getter
    @Setter
    private String shaCode;

    /**
     * 文件名
     */
    @Getter
    @Setter
    private String name;

    /**
     * 书签表
     */
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "markFor",fetch = FetchType.EAGER)
    private Set<BookMark> marks;

    /**
     * 目录
     */
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "located",fetch = FetchType.EAGER)
    private Set<ContentsItem> contentsItems;

    /**
     * 文件大小
     */
    @Getter
    @Setter
    private String size;

    /**
     * 分类
     */
    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH)
    private BookType type;

    /**
     * 文件的mime，用于读取文件
     */
    @Getter
    @Setter
    private String mimeData;

    /**
     * 创建日期
     */
    @Getter
    @Setter
    private Date createDate;

    /**
     * 出版社
     */
    @Getter
    @Setter
    private String publisher;

    /**
     * 作者
     */
    @Getter
    @Setter
    private String author;

    /**
     * 标签
     */
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST},fetch = FetchType.EAGER)
    private Set<BookTag> tags;
}

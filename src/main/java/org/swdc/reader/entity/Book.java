package org.swdc.reader.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * 书
 */
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
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "markFor")
    private Set<BookMark> marks;

    /**
     * 目录
     */
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "located")
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

}

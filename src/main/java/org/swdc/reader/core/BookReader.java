package org.swdc.reader.core;

import javafx.scene.layout.BorderPane;
import org.swdc.reader.core.ext.ExternalRenderAction;
import org.swdc.reader.entity.Book;

/**
 * 核心组件，提供读取内容的locator
 * 渲染内容的方法以及判断是否支持的方法
 */
public interface BookReader<T> {

    String getIndexedMode();

    /**
     * 设置book对象，这个时候应该为book创建Locator
     * @param book
     */
    void setBook(Book book);

    BookView getView();

    /**
     * 获取当前的book
     * @return
     */
    Book getBook();

    /**
     * 在View中进行页面渲染
     * @param pageData
     * @param view
     */
    void renderPage(T pageData, BorderPane view);

    /**
     * 此Reader是否支持当前类型的book
     * @param target
     * @return
     */
    boolean isSupport(Book target);

    /**
     * 添加额外的渲染器
     * @param action 额外的渲染器实例
     */
    void addRenderAction(ExternalRenderAction action);

    /**
     * 移除渲染器
     * @param action 渲染器类型
     * @param <T>
     * @return
     */
    <T> T removeRenderAction(Class<T> action);

    /**
     * 获取Locator来读取数据
     * @return
     */
    BookLocator<T> getLocator();

    /**
     * 释放资源
     */
    default void finalizeResources() {
        getLocator().finalizeResources();
    }
}

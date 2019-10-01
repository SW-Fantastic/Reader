package org.swdc.reader.core;


/**
 * Created by lenovo on 2019/5/28.
 */
public interface BookLocator<T> {

    /**
     * 返回当前页的上一页
     * @return
     */
    T prevPage();

    /**
     * 返回当前页的下一页
     * @return
     */
    T nextPage();

    /**
     * 跳转到此页
     * @param location
     * @return
     */
    T toPage(String location);

    String getTitle();

    String getLocation();

    /**
     * 读取当前页
     * @return
     */
    T currentPage();

    /**
     * 释放资源
     * locator一般会一直打开一个文件用于读取内容
     * 有的时候还会缓存内容以加快再次载入的速度
     * 不需要这些的时候应该释放掉
     */
    void finalizeResources();

    Boolean isAvailable();

}

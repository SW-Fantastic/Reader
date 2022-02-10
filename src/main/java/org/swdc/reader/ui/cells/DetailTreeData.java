package org.swdc.reader.ui.cells;

import org.swdc.reader.services.BookServices;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 这个是放在首页的Tree中的数据类。
 * 它的作用只有一个，就是通过本类的对象提供的callback进行
 * 书籍的加载。
 *
 * 和分类列表不同，到底加载什么全看Callback怎么写，这个
 * 灵活度是非常高的。
 */
public class DetailTreeData {

    /**
     * 在此TreeItem被选中的时候，执行本函数。
     */
    private Consumer<BookServices> bookLoader = null;

    private Object item = null;

    private String fieldName;

    public DetailTreeData(Consumer<BookServices> loader, Object item, String fieldName) {
        this.fieldName = fieldName;
        this.bookLoader = loader;
        this.item = item;
    }

    public void call(BookServices services) {
        if (this.bookLoader == null) {
            return;
        }
        this.bookLoader.accept(services);
    }

    @Override
    public String toString() {
        if (this.item == null) {
            return null;
        }
        if (this.fieldName != null && !this.fieldName.isEmpty()) {
            try {
                Field field = item.getClass().getDeclaredField(this.fieldName);
                field.setAccessible(true);
                String text = field.get(item).toString();
                return text;
            } catch (Exception e) {
                throw new RuntimeException("字段" + fieldName + "不存在");
            }
        }
        return item.toString();
    }
}

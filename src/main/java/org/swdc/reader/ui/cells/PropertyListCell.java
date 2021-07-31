package org.swdc.reader.ui.cells;

import javafx.scene.control.ListCell;

import java.lang.reflect.Field;

public class PropertyListCell<T> extends ListCell<T> {

    private String fieldName;

    public PropertyListCell(String property) {
        this.fieldName = property;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
            return;
        }
        try {
            Field field = item.getClass().getDeclaredField(this.fieldName);
            field.setAccessible(true);
            String text = field.get(item).toString();
            this.setText(text);
        } catch (Exception e) {
            throw new RuntimeException("字段" + fieldName + "不存在");
        }
    }
}

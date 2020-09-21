package org.swdc.reader.core;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * 提供Reader使用的view界面，不同格式的
 * 电子书可能需要不同的界面进行渲染。
 */
public interface BookView {

    /**
     * 返回渲染使用的界面，这个界面会放在ReadView的中央
     * @return JavaFX的界面Node
     */
    Node getView();

    /**
     * 在readerView的工具栏配置工具组件
     */
    Node getToolsView();
}

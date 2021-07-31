package org.swdc.reader.ui.dialogs.reader;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;

import java.util.List;

/**
 * 这个dialog比较特殊。
 * 他是目录和书签的Dialog，此Dialog在使用的时候必然需要一个BookReader对象，
 * 同样BookReader对象也需要此Dialog来记载目录和书签。
 *
 * 由于Reader在此版本不能直接注入，所以关于目录和书签的功能就都在这里面了。
 * Reader通过本类的buildTableOfContent的方法创建目录项，通过本类的Controller的
 * saveMark方法记录书签。
 *
 */
@View(viewLocation = "/views/dialogs/TOCAndFavorite.fxml",
        title = "目录和书签",dialog = true,resizeable = false)
public class TOCAndFavoriteDialog extends AbstractView {


    public void buildTableOfContents(BookReader reader) {
        TOCAndFavoriteDialogController controller = this.getController();
        controller.setReader(reader);
        controller.buildTableOfContent();
    }

    public void buildTableOfContent(ContentsItem item) {
        TOCAndFavoriteDialogController controller = this.getController();
        controller.buildTableOfContent(item);
    }

    public void showTableOfContent(BookReader readerView) {
        TOCAndFavoriteDialogController controller = this.getController();
        controller.setReader(readerView);

        TabPane tabs = this.findById("tabs");
        List<Tab> tabList = tabs.getTabs();
        for (Tab tab:tabList) {
            if (tab.getId().equals("toc")) {
                tabs.getSelectionModel().select(tab);
                break;
            }
        }

        HBox box = this.findById("markTools");
        box.setDisable(readerView == null);

        this.show();
    }

    public boolean hasTableOfContents(BookReader reader) {
        TOCAndFavoriteDialogController controller = this.getController();
        controller.setReader(reader);
        return controller.hasTableOfContents();
    }

    public void showMarks(BookReader readerView) {
        TOCAndFavoriteDialogController controller = this.getController();
        controller.setReader(readerView);

        TabPane tabs = this.findById("tabs");
        List<Tab> tabList = tabs.getTabs();
        for (Tab tab:tabList) {
            if (tab.getId().equals("marks")) {
                tabs.getSelectionModel().select(tab);
                break;
            }
        }
        HBox box = this.findById("markTools");
        box.setDisable(readerView == null);
        this.show();
    }

    @Override
    public void hide() {
        super.hide();
        TOCAndFavoriteDialogController controller = this.getController();
        controller.setReader(null);
    }
}

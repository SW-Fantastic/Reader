package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import org.controlsfx.control.GridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.ui.cells.BookGridCell;
import org.swdc.reader.ui.cells.TypeCell;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by lenovo on 2019/5/19.
 */
@FXMLView("/views/BooksView.fxml")
public class BooksView extends AbstractFxmlView {

    @Getter
    private GridView<Book> bookGridView;

    @Autowired
    private ApplicationConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        UIUtils.configUI((BorderPane)this.getView(), config);
        BorderPane pane = (BorderPane)this.getView();
        GridView<Book> gridView = new GridView<>();
        BorderPane borderPane = (BorderPane)pane.getCenter();
        borderPane.setCenter(gridView);
        gridView.setCellFactory(view->new BookGridCell(config.getComponent(BookCellView.class)));
        gridView.setCellWidth(170);
        gridView.setCellHeight(210);
        this.bookGridView = gridView;

        this.setButtonIcon("btnSearch", "search");
        this.setButtonIcon("btnOpen", "folder_open");
        this.setButtonIcon("btnRefresh", "refresh");
        this.setButtonIcon("btnType", "plus");

        ListView<BookType> typeList = (ListView)pane.lookup("#typelist");
        typeList.setCellFactory(list->new TypeCell(config.getComponent(TypeCellView.class)));
    }

    private void setButtonIcon(String btnId,String iconName) {
        Button btn = (Button)this.getView().lookup("#" + btnId);
        if (btn == null) {
            return;
        }
        btn.setFont(AwsomeIconData.getFontIcon());
        btn.setText("" + AwsomeIconData.getAwesomeMap().get(iconName));
    }

}

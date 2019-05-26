package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.ui.views.dialog.BookEditDialog;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/22.
 */
@Scope("prototype")
@FXMLView("/views/BookCellView.fxml")
public class BookCellView extends AbstractFxmlView{

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private BookEditDialog editDialog;

    protected Book book;

    @PostConstruct
    protected void initUI() throws Exception {
        UIUtils.configUI((BorderPane)getView(), config);
        BorderPane pane = (BorderPane)this.getView();
        HBox bottom = (HBox)pane.getBottom();

        setButtonIcon("btnOpen", "folder_open", bottom.getChildren(), this::onOpen);
        setButtonIcon("btnContents", "bars", bottom.getChildren(), this::onContents);
        setButtonIcon("btnMark", "tag", bottom.getChildren(), this::onTag);
        setButtonIcon("btnEdit", "pencil", bottom.getChildren(), this::onBookEdit);

    }

    private void onBookEdit(ActionEvent event) {
        editDialog.setBook(book);
        editDialog.show();
    }

    private void onOpen(ActionEvent event) {

    }

    private void onContents(ActionEvent event) {

    }

    private void onTag(ActionEvent event) {

    }

    private void setButtonIcon(String id, String iconName, ObservableList<Node> childs, EventHandler<ActionEvent> handler) {
        Button btn = UIUtils.findById(id, childs);
        btn.setFont(AwsomeIconData.getFontIconSmall());
        btn.setText(AwsomeIconData.getAwesomeMap().get(iconName) + "");
        btn.setOnAction(handler);
    }

    public void setBook(Book book) {
        this.book = book;
        if (book == null) {
            return;
        }
        BorderPane pane = (BorderPane)this.getView();
        HBox top = (HBox)pane.getTop();
        Label label = UIUtils.findById("title", top.getChildren());
        label.setText(book.getTitle());

        GridPane gridPane = (GridPane) pane.getCenter();
        Label lblSize = (Label)gridPane.lookup("#fileSize");
        lblSize.setText(book.getSize());
    }

}

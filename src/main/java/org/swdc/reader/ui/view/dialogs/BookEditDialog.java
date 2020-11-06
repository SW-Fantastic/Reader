package org.swdc.reader.ui.view.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import lombok.Getter;
import org.controlsfx.control.GridView;
import org.controlsfx.control.PopOver;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.view.cells.TagCell;
import org.swdc.reader.ui.view.cells.TagCellView;
import org.swdc.reader.ui.view.cells.TagGridCell;

import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2019/5/26.
 */
@View(title = "lang@dialog-book-title",dialog = true)
public class BookEditDialog extends FXView {

    @Getter
    private Book book;

    private PopOver popOver;

    private ListView<BookTag> tagPopupListView;

    @Getter
    private ObservableList<BookTag> tags = FXCollections.observableArrayList();

    @Aware
    private FontawsomeService fontawsomeService = null;

    @Override
    public void initialize() {

        this.createGridView();

        Button addTag = findById("tagAdd");
        addTag.setFont(fontawsomeService.getFont(FontSize.MIDDLE_SMALL));
        addTag.setPadding(new Insets(4,4,4,4));
        addTag.setText(fontawsomeService.getFontIcon("plus_circle"));

        TextField txtTag = findById("txtTag");
        BookService service = findService(BookService.class);

        tagPopupListView = new ListView<>();
        ObservableList<BookTag> tags = tagPopupListView.getItems();
        popOver = new PopOver(tagPopupListView);
        popOver.getStyleClass().add("tag-popup");
        tagPopupListView.setPrefHeight(120);
        tagPopupListView.setCellFactory((lv) -> new TagCell(findView(TagCellView.class)
                .withAction(this::onTagsDeleted)));
        tagPopupListView.setOnMouseClicked(e-> {
            BookTag tag = tagPopupListView.getSelectionModel().getSelectedItem();
            if (tag == null) {
                return;
            }
            this.tags.add(tag);
            txtTag.clear();
            popOver.hide();
        });
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        txtTag.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                tags.clear();
                popOver.hide();
            }
        }));

        txtTag.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!popOver.isShowing()) {
                popOver.show(txtTag);
            }
            tags.clear();
            List<BookTag> tagList = service.getTags(txtTag.getText());
            if (this.getBook() != null) {
                tagList.removeAll(this.tags);
            }
            tags.addAll(tagList);
        }));

        this.getStage().setOnCloseRequest(e -> {
            popOver.hide(Duration.ZERO);
        });

    }

    private GridView<BookTag> createGridView() {
        BorderPane tagsContainer = findById("tabsContainer");
        GridView<BookTag> tagGridView = new GridView<>();
        tagGridView.setCellHeight(24);
        tagGridView.setCellWidth(82);
        tagGridView.setHorizontalCellSpacing(2);
        tagGridView.setVerticalCellSpacing(2);
        tagGridView.setItems(tags);
        tagGridView.getStyleClass().add("tag-list");
        tagsContainer.setCenter(tagGridView);

        tagGridView.setCellFactory((gv) -> new TagGridCell(findView(TagCellView.class)
                .withAction(this::onTagsRemove)));

        return tagGridView;
    }

    public void onTagsRemove(TagCellView cellView, ActionEvent event) {
        if (cellView.getTag() != null) {
            tags.remove(cellView.getTag());
        }
    }

    public void onTagsDeleted(TagCellView cellView, ActionEvent event) {
        BookService service = findService(BookService.class);
        if (cellView.getTag() != null) {
            BookTag tag = cellView.getTag();
            service.removeTag(tag);
            tags.remove(tag);
            tagPopupListView.getItems().remove(tag);
        }
    }

    public PopOver getPopOver() {
        return popOver;
    }

    public void setBook(Book book) {
        TextField txtTitle =this.findById("txtTitle");
        TextField txtPublisher = this.findById("txtPublisher");
        TextField txtAuthor = this.findById("txtAuthor");
        ComboBox<BookType> typeComboBox = this.findById("cbxType");
        TextField txtTag = findById("txtTag");

        txtTag.setText("");
        tags.clear();

        if (book != null){
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "");
            txtPublisher.setText(book.getPublisher() != null ? book.getPublisher():"");
            typeComboBox.getSelectionModel().select(book.getType());
            Set<BookTag> tagExists = book.getTags();
            if (tagExists != null) {
                tags.addAll(tagExists);
            }
        } else {
            txtTitle.setText("");
            txtAuthor.setText("");
            txtPublisher.setText("");
            typeComboBox.getSelectionModel().select(null);
        }
        this.book = book;
    }

}

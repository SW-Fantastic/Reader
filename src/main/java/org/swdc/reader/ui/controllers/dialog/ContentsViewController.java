package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.ContentItemChangeEvent;
import org.swdc.reader.services.BookService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/6/5.
 */
@FXMLController
public class ContentsViewController implements Initializable {

    @FXML
    protected ListView<ContentsItem> itemList;

    @Autowired
    private BookService service;

    private ObservableList<ContentsItem> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemList.setItems(items);
    }

    @EventListener(ContentItemChangeEvent.class)
    public void onItemsChange(ContentItemChangeEvent event) {
        Book book = service.getBook(event.getSource().getId());
        this.items.clear();
        this.items.addAll(book.getContentsItems());
        this.items.sort((itemA, itemB) -> itemA.getId().intValue() - itemB.getId().intValue());
    }

}

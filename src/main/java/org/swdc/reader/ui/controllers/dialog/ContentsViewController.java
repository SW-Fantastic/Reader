package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BookLocationEvent;
import org.swdc.reader.event.ContentItemChangeEvent;
import org.swdc.reader.event.DocumentOpenEvent;
import org.swdc.reader.event.ViewChangeEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.dialog.ContentsItemView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 目录对话框的控制器
 */
@FXMLController
public class ContentsViewController implements Initializable {

    @FXML
    protected ListView<ContentsItem> itemList;

    @Autowired
    private ContentsItemView view;

    @Autowired
    private BookService service;

    @Autowired
    private ApplicationConfig config;

    private ObservableList<ContentsItem> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemList.setItems(items);
    }

    @EventListener(ContentItemChangeEvent.class)
    public void onItemsChange(ContentItemChangeEvent event) {
        this.resolveBookContent(event.getSource());
    }

    @EventListener(DocumentOpenEvent.class)
    public void onDocumentOpen(DocumentOpenEvent event) {
       this.resolveBookContent(event.getSource());
    }

    private void resolveBookContent(Book booktarget) {
        Platform.runLater(() -> {
            Book book =  service.getBook(booktarget.getId());
            this.items.clear();
            this.items.addAll(book.getContentsItems());
            this.items.sort((itemA, itemB) -> itemA.getId().intValue() - itemB.getId().intValue());
        });
    }

    @FXML
    protected void onCancel() {
        view.close();
    }

    @FXML
    protected void onOK() {
        ContentsItem item = this.itemList.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        Book targetBook = item.getLocated();
        // 切换文档
        config.publishEvent(new DocumentOpenEvent(targetBook));
        // 切换位置
        config.publishEvent(new BookLocationEvent(item.getLocation()));
        // 切换视图
        config.publishEvent(new ViewChangeEvent("read"));
        view.close();
    }

}

package org.swdc.reader.ui.controller.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.core.event.BookLocationEvent;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.events.ContentItemChangeEvent;
import org.swdc.reader.ui.events.DocumentOpenEvent;
import org.swdc.reader.ui.events.ViewChangeEvent;
import org.swdc.reader.ui.view.dialogs.ContentsItemView;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ContentsViewController extends FXController {

    @FXML
    protected ListView<ContentsItem> itemList;

    @Aware
    private BookService service = null;

    private ObservableList<ContentsItem> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemList.setItems(items);
    }

    @Listener(value = ContentItemChangeEvent.class,updateUI = true)
    public void onItemsChange(ContentItemChangeEvent event) {
        this.resolveBookContent(event.getData());
    }

    @Listener(value = DocumentOpenEvent.class,updateUI = true)
    public void onDocumentOpen(DocumentOpenEvent event) {
        this.resolveBookContent(event.getData());
    }

    private void resolveBookContent(Book booktarget) {
        Book book =  service.getBook(booktarget.getId());
        this.items.clear();
        this.items.addAll(book.getContentsItems());
        this.items.sort(Comparator.comparingInt(itemA -> itemA.getId().intValue()));
    }

    @FXML
    public void onOK() {
        ContentsItem item = this.itemList.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        ContentsItemView view = getView();
        Book targetBook = item.getLocated();
        // 切换文档
        this.emit(new DocumentOpenEvent(targetBook,this));
        // 切换位置
        this.emit(new BookLocationEvent(item.getLocation(),this));
        // 切换视图
        this.emit(new ViewChangeEvent("read",this));
        view.close();
    }

    @FXML
    public void onCancel() {
        ContentsItemView view = getView();
        view.close();
    }

}

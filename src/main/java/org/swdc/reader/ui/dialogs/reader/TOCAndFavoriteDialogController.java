package org.swdc.reader.ui.dialogs.reader;

import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.services.BookMarksServices;
import org.swdc.reader.services.TableOfContentServices;
import org.swdc.reader.ui.cells.PropertyListCell;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TOCAndFavoriteDialogController implements Initializable {

    @Inject
    private TOCAndFavoriteDialog dialog;

    @Inject
    private BookMarksServices bookMarksServices;

    @Inject
    private TableOfContentServices tableOfContentServices;

    @FXML
    private ListView<ContentsItem> contentsList;

    @FXML
    private ListView<BookMark> bookMarks;

    @FXML
    private TextField txtMarkName;

    private ObservableList<ContentsItem> tocList = FXCollections.observableArrayList();

    private ObservableList<BookMark> bookMarksList = FXCollections.observableArrayList();

    private BookReader reader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bookMarks.setItems(bookMarksList);
        this.bookMarks.setCellFactory(l -> new PropertyListCell<>("description"));
        this.contentsList.setItems(tocList);

        this.bookMarksList.clear();
        this.tocList.clear();

        this.contentsList.setOnMouseClicked(this::onContentsClick);
        this.bookMarks.setOnMouseClicked(this::onMarksClick);
    }

    private void onMarksClick(MouseEvent e) {
        int clickCount = e.getClickCount();
        if (clickCount < 2 || e.getButton() != MouseButton.PRIMARY) {
            return;
        } else {
            if (this.reader == null){
                return;
            }
            BookMark item = bookMarks.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
            this.reader.goTo(item.getLocation());
        }
    }

    private void onContentsClick(MouseEvent e){
        int clickCount = e.getClickCount();
        if (clickCount < 2 || e.getButton() != MouseButton.PRIMARY) {
            return;
        } else {
            if (this.reader == null){
                return;
            }
            ContentsItem item = contentsList.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
            this.reader.goTo(item.getLocation());
        }
    }

    @FXML
    public void onClose() {
        this.dialog.hide();
    }

    @FXML
    public void saveMark() {
        if (this.reader == null) {
            return;
        }
        String desc = this.txtMarkName.getText();
        if (desc == null || desc.isEmpty()) {
            return;
        }
        String location = reader.getLocation();
        if (location == null) {
            return;
        }
        bookMarksServices.create(reader.getBook(), location,desc);
        this.refreshLists();
    }

    public void setReader(BookReader reader) {
        this.reader = reader;
        this.refreshLists();
    }

    public void buildTableOfContent(ContentsItem item) {
        if (item.getLocation() == null ||
                item.getLocation().isEmpty() ||
                item.getLocated() == null ||
                item.getTitle() == null||
                item.getTitle().isEmpty()) {
            return;
        }

        this.tableOfContentServices.create(item.getTitle(),item.getLocation(),item.getLocated());
    }

    public void buildTableOfContent() {
        if (this.reader == null) {
            return;
        }
        Book book = this.reader.getBook();
        String location = this.reader.getLocation();
        String title = this.reader.getChapterName();

        if (book == null || location == null || location.isEmpty() || title == null || title.isEmpty())  {
            return;
        }

        tableOfContentServices.create(title,location,book);
        this.refreshLists();

    }


    public Boolean hasTableOfContents() {
        return this.tableOfContentServices.hasTableOfContents(reader.getBook());
    }

    public void refreshLists() {
        this.bookMarksList.clear();
        this.tocList.clear();
        if (this.reader == null) {
           return;
        }
        Book book = this.reader.getBook();
        List<BookMark> marks = this.bookMarksServices.getMarks(book);
        this.bookMarksList.addAll(marks);

        List<ContentsItem> items = this.tableOfContentServices.getTableOfContent(book);
        this.tocList.addAll(items);
    }


}

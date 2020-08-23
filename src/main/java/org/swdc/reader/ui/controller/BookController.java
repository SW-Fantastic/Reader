package org.swdc.reader.ui.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.events.*;
import org.swdc.reader.ui.view.MainView;
import org.swdc.reader.ui.view.cells.BookEditCell;
import org.swdc.reader.ui.view.cells.BookEditCellView;
import org.swdc.reader.ui.view.dialogs.BookImportDialog;
import org.swdc.reader.ui.view.dialogs.TypeAddDialog;

import java.io.File;
import java.util.List;
import java.util.Set;

public class BookController extends FXController {

    @Aware
    private BookService service = null;

    @Aware
    private TypeAddDialog addDialog = null;

    @Aware
    private BookImportDialog importView = null;

    @FXML
    private ListView<BookType> typeListView;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Book> detailTable;

    @FXML
    private TableColumn<Book, String> colName;

    @FXML
    private TableColumn<Book, String> colSize;

    @FXML
    private TableColumn<Book, String> colSha;

    @FXML
    private TableColumn<Book, Void> colEdit;

    private ObservableList<BookType> bookTypes = FXCollections.observableArrayList();

    private ObservableList<Book> books = FXCollections.observableArrayList();

    @Override
    public void initialize() {
        typeListView.setItems(bookTypes);
        typeListView.getSelectionModel().selectedItemProperty().addListener(this::typeChange);
        List<BookType> types = service.listTypes();
        bookTypes.clear();
        bookTypes.addAll(types);

        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colSha.setCellValueFactory(new PropertyValueFactory<>("shaCode"));
        colEdit.setCellFactory(view->new BookEditCell(findView(BookEditCellView.class)));
        detailTable.setPlaceholder(new Label("还没有任何书籍数据"));
        detailTable.setItems(books);

        detailTable.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                Book book = detailTable.getSelectionModel().getSelectedItem();
                if (book == null) {
                    return;
                }
                // 切换视图
                this.emit(new ViewChangeEvent("read",this));
                // 切换文档
                this.emit(new DocumentOpenEvent(book,this));
                // 切换目录
                this.emit(new ContentItemChangeEvent(book,this));
            }
        });
    }

    private void typeChange(ObservableValue<? extends BookType> typeObservableValue, BookType old, BookType newVal) {
        this.emit(new BooksRefreshEvent(this));
    }

    @Listener(value = BooksRefreshEvent.class,updateUI = true)
    public void onRefreshList(BooksRefreshEvent event) {
        BookType type = typeListView.getSelectionModel().getSelectedItem();
        books.clear();
        if (type == null) {
            BookType defaultType = service.getDefaultType();
            books.addAll(defaultType.getBooks());
        } else {
            books.addAll(service.getBooks(type));
            typeListView.getSelectionModel().select(type);
        }
    }


    @Listener(value = TypeRefreshEvent.class,updateUI = true)
    public void onTypeRefresh(TypeRefreshEvent event) {
        List<BookType> types = service.listTypes();
        bookTypes.clear();
        bookTypes.addAll(types);
    }

    @FXML
    public void onAddType() {
        addDialog.show();
    }

    @FXML
    public void onOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("打开");
        File target = chooser.showOpenDialog(findView(MainView.class).getStage());
        if (target == null || !target.exists() || target.isDirectory()) {
            return;
        }
        importView.setBook(target);
        importView.show();
    }

    @FXML
    public void onSyncFiles() throws Exception {
        service.syncBookFolder();
    }

    @FXML
    public void onTextSearchChange() {
        if (txtSearch.getText().trim().equals("")) {
            books.clear();
            this.emit(new BooksRefreshEvent(this));
        }
    }

    @FXML
    public void onSearch(){
        if (txtSearch.getText().trim().equals("")) {
            return;
        }
        Set<Book> bookSearched = service.searchByName(txtSearch.getText());
        books.clear();
        books.addAll(bookSearched);
    }

}

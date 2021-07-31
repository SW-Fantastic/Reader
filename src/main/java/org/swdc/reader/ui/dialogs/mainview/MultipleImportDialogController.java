package org.swdc.reader.ui.dialogs.mainview;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.StageCloseEvent;
import org.swdc.fx.view.Toast;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.TreeRefreshEvent;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.services.BookServices;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.services.TypeServices;
import org.swdc.reader.ui.cells.MultipleImportEditCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MultipleImportDialogController implements Initializable {

    @Inject
    private TypeServices typeServices;

    @Inject
    private BookServices  bookServices;

    @Inject
    private MultipleImportDialog view;

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

    @FXML
    private TableView<Book> externBookTable;

    @FXML
    private TableColumn<Book,String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book,BookType> typeColumn;

    @FXML
    private TableColumn<Book,String> publisherColumn;

    @FXML
    private TableColumn<Book,Void> editColumn;

    private ObservableList<BookType> typeList = FXCollections.observableArrayList();

    private ObservableList<Book> externalBooks = FXCollections.observableArrayList();

    private File folder;

    @PostConstruct
    public void dataInit() {
        typeList.addAll(typeServices.allTypes());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.externBookTable.setItems(this.externalBooks);
        this.externBookTable.setEditable(true);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        authorColumn.setOnEditCommit(e -> {
            int row =  e.getTablePosition().getRow();
            e.getTableView()
                    .getItems()
                    .get(row)
                    .setAuthor(e.getNewValue());
        });

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(typeList));
        typeColumn.setOnEditCommit(e -> {
            int row =  e.getTablePosition().getRow();
            e.getTableView()
                    .getItems()
                    .get(row)
                    .setType(e.getNewValue());
        });

        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        publisherColumn.setEditable(true);
        publisherColumn.setOnEditCommit(e -> {
            int row =  e.getTablePosition().getRow();
            e.getTableView()
                    .getItems()
                    .get(row)
                    .setPublisher(e.getNewValue());
        });

        editColumn.setEditable(false);
        editColumn.setCellFactory(c -> new MultipleImportEditCell(this.view));
    }

    @FXML
    public void cancel(){
        this.view.hide();
    }

    @FXML
    public void importAll() {
        this.view.getView().setDisable(true);
        List<Book> removed = new ArrayList<>();
        for (Book item: externalBooks) {
            if (this.doImport(item)) {
                removed.add(item);
            }
        }
        externalBooks.removeAll(removed);
        this.view.getView().setDisable(false);
        this.view.emit(new TypeListRefreshEvent(""));
        this.view.emit(new TreeRefreshEvent(""));
    }

    /**
     * 监听TypeList的变化事件。
     * Type出现刷新或者改变的时候将会触发。
     * @param event
     */
    @EventListener(type = TypeListRefreshEvent.class)
    public void refreshTypeLists(TypeListRefreshEvent event) {
        typeList.clear();
        List<BookType> typesList = typeServices.allTypes();
        this.typeList.clear();
        this.typeList.addAll(typesList);

    }

    @EventListener(type = MultipleImportEditCell.BookImportEvent.class)
    public void onBookImportRequest(MultipleImportEditCell.BookImportEvent event) {
        Book target = event.getMessage();
        if (this.doImport(target)) {
            this.externalBooks.remove(target);
        }
    }

    private boolean doImport(Book target) {
        if (target == null) {
            return false;
        }
        if (target.getType() == null) {
            Alert alert = this.view.alert("提示","请为文档《" + target.getName() + "》选择一个分类，双击表格可以进行编辑。", Alert.AlertType.ERROR);
            alert.showAndWait();
            return false;
        }
        try {
            File file = resources.getAssetsFolder();
            Path bookFolder = file.toPath().resolve("library");
            if (!Files.exists(bookFolder)) {
                bookFolder = Files.createDirectories(bookFolder);
            }
            Path from = this.folder.toPath().resolve(target.getName());
            Path to = bookFolder.resolve(target.getName());
            if (!from.equals(to)) {
                FileInputStream fin = new FileInputStream(this.folder.toPath().resolve(target.getName()).toFile());
                OutputStream fout = Files.newOutputStream(bookFolder.resolve(target.getName()));
                fin.transferTo(fout);
                fin.close();
                fout.close();
            }

            Book book = new Book();
            book.setName(target.getName());
            book.setTitle(target.getName());
            book.setCreateDate(new Date());
            book.setType(target.getType());
            book.setMimeData(target.getMimeData());
            book.setPublisher(target.getPublisher());
            book.setAuthor(target.getAuthor());
            book.setSize(target.getSize());
            book.setShaCode(target.getShaCode());

            bookServices.createBook(book);

            this.view.emit(new TypeListRefreshEvent(""));
            this.view.emit(new TreeRefreshEvent(""));
            return true;
        } catch (Exception e) {
            logger.error("导入失败",e);
            return false;
        }
    }

    public void setFolder(File folder) {
        if (folder.isFile()) {
            return;
        }

        this.externalBooks.clear();
        this.view.getView().setDisable(true);
        Toast.showMessage("载入中，请稍候。");
        resources.getExecutor().submit(() -> {
            this.folder = folder;
            List<Book> books = new ArrayList<>();
            File[] files = this.folder.listFiles();
            for (File file: files) {
                try {

                    if (bookServices.isBookFileExist(file)) {
                        continue;
                    }

                    Book book = new Book();
                    book.setTitle(file.getName());
                    book.setName(file.getName());

                    Magic.initialize();
                    MagicMatch magicMatch = Magic.getMagicMatch(file,true,false);
                    book.setMimeData(magicMatch.getMimeType());
                    book.setShaCode(bookServices.sha256(file.toPath()));
                    book.setSize(bookServices.getSize(file.length()));

                    books.add(book);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Platform.runLater(() -> {
                externalBooks.addAll(books);
                this.view.getView().setDisable(false);
            });
        });

    }

    @EventListener(type = StageCloseEvent.class)
    public void onStageClose(StageCloseEvent closeEvent) {
        if (closeEvent.getMessage() != this.view.getClass()) {
            return;
        }
        this.folder = null;
        this.externalBooks.clear();
    }

}

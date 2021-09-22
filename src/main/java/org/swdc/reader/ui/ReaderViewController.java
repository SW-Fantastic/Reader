package org.swdc.reader.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Mnemonic;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.view.Toast;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.BookWillRemoveEvent;
import org.swdc.reader.events.TreeRefreshEvent;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.events.TypeSelectEvent;
import org.swdc.reader.services.BookServices;
import org.swdc.reader.services.BookTagServices;
import org.swdc.reader.services.TypeServices;
import org.swdc.reader.ui.cells.BookControlCell;
import org.swdc.reader.ui.cells.DetailTreeData;
import org.swdc.reader.ui.dialogs.mainview.*;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ReaderViewController implements Initializable {


    // ----  UI和view  ---- //

    @Inject
    private MainView mainView;

    @Inject
    private ReaderView readerView;

    @Inject
    private AddTypeDialog addTypeDialog;

    @Inject
    private ImportFileDialog fileDialog;

    @Inject
    private EditTypeDialog editTypeDialog;

    // ---- Service 组件 ---- //

    @Inject
    private FontawsomeService iconService;

    @Inject
    private TypeServices typeServices;

    @Inject
    private BookServices bookServices;

    @Inject
    private BookTagServices tagServices;

    @Inject
    private BookEditDialog editDialog;

    @Inject
    private Logger logger;

    @Inject
    private FXResources resources;

    @Inject
    private MultipleImportDialog multipleImportDialog;

    // ---- FXML JavaFX注入 ---- //

    @FXML
    private TableColumn<Book,String> titleColumn;

    @FXML
    private TableColumn<Book, Date> createColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book,String> publisherColumn;

    @FXML
    private TableColumn<Book,Void> editColumn;

    @FXML
    private TableView<Book> bookTableView;

    @FXML
    private Button searchToolButton;

    @FXML
    private ToolBar toolbar;

    @FXML
    private TabPane tabs;

    @FXML
    private ListView<BookType> typeList;

    @FXML
    private TreeView<DetailTreeData> detailsTree;

    private PopOver search;

    private TextField searchField;

    private TreeItem<DetailTreeData> root;

    private TreeItem<DetailTreeData> author;

    private TreeItem<DetailTreeData> publisher;

    private TreeItem<DetailTreeData> tags;

    @Inject
    private List<BookDescriptor> bookDescriptors;

    private ObservableList<BookType> types = FXCollections.observableArrayList();

    private ObservableList<Book> books = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        createColumn.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        editColumn.setCellFactory((col) -> new BookControlCell(iconService, this.readerView));
        bookTableView.setItems(books);


        ContextMenu menu = new ContextMenu();

        BooleanProperty menuItemEnabled = new SimpleBooleanProperty();
        MenuItem open = new MenuItem("打开");
        open.disableProperty().bindBidirectional(menuItemEnabled);
        open.setOnAction((e) -> this.openBook(this.bookTableView.getSelectionModel().getSelectedItem()));
        MenuItem edit = new MenuItem("编辑文档信息");
        edit.disableProperty().bindBidirectional(menuItemEnabled);
        edit.setOnAction((e) -> editDialog.showWithBook(this.bookTableView.getSelectionModel().getSelectedItem()));
        MenuItem remove = new MenuItem("删除");
        remove.disableProperty().bindBidirectional(menuItemEnabled);
        remove.setOnAction((e) -> this.tableCellRemoveClicked(
                new BookControlCell.BookRemoveEvent(this.bookTableView.getSelectionModel().getSelectedItem())
        ));

        MenuItem add = new MenuItem("导入");
        add.setOnAction((e) -> this.showOpenFile());

        menu.getItems().addAll(open,edit,remove,new SeparatorMenuItem(),add);
        bookTableView.setContextMenu(menu);

        bookTableView.setOnMouseClicked( e -> {
            if (bookTableView.getSelectionModel().getSelectedItem() == null) {
                menuItemEnabled.set(true);
            } else {
                menuItemEnabled.set(false);
            }
            if (e.getClickCount() < 2) {
                return;
            }
            this.openBook(bookTableView.getSelectionModel().getSelectedItem());
        });


        root = new TreeItem<>();
        this.tags = new TreeItem<>(new DetailTreeData(null,"书签",null));
        root.getChildren().add(tags);

        this.author = new TreeItem<>(new DetailTreeData(null,"作者",null));
        root.getChildren().add(author);

        this.publisher = new TreeItem<>(new DetailTreeData(null,"出版发行",null));
        root.getChildren().add(publisher);

        this.typeList.setOnMouseClicked(e ->{
            this.detailsTree.getSelectionModel().clearSelection();
        });

        root.setExpanded(true);
        detailsTree.setRoot(root);
        detailsTree.setShowRoot(false);
        detailsTree.setOnMouseClicked(e -> {
            this.typeList.getSelectionModel().clearSelection();
            TreeItem<DetailTreeData> data = detailsTree.getSelectionModel().getSelectedItem();
            if (data == null) {
                return;
            }
            DetailTreeData target = data.getValue();
            if (target == null) {
                return;
            }
            target.call(this.bookServices);
        });

        this.treeRefresh(null);
        this.search = new PopOver();
        this.searchField = new TextField();
        searchField.setPrefWidth(240);

        this.search.setContentNode(searchField);
        this.search.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        this.search.setHideOnEscape(true);
        this.toolbar.setOnMouseExited(this::beforeHide);

        this.searchField.textProperty().addListener(e -> {
            String prefix = searchField.getText();
            this.books.clear();
            if (prefix == null || prefix.isEmpty()) {
                return;
            }
            List<Book> books = bookServices.searchByName(prefix);
            this.books.addAll(books);
        });
        this.tabs.addEventFilter(KeyEvent.KEY_PRESSED,ev -> {
            if(ev.isConsumed()) {
                return;
            }

            Tab tab = this.tabs.getSelectionModel().getSelectedItem();
            if (tab.getUserData() == null) {
                return;
            }
            BookReader reader = (BookReader) tab.getUserData();
            if (ev.getCode() == KeyCode.LEFT) {
                reader.goPreviousPage();
                ev.consume();
            } else if (ev.getCode() == KeyCode.RIGHT) {
                reader.goNextPage();
                ev.consume();
            }
        });
    }

    public void beforeHide(Object o) {
        this.search.hide(Duration.ZERO);
    }

    public void openBook(Book book){

       if (book == null) {
            return;
       }

       readerView.getView().setDisable(true);
       // 切换到线程池，防止卡UI
       resources.getExecutor().execute(() -> {

           BookDescriptor desc = bookDescriptors
                   .stream()
                   .filter(d -> d.support(book)).findFirst()
                   .orElse(null);

           if (desc == null) {
               Platform.runLater(() -> readerView.getView().setDisable(false));
               return;
           }

           File bookFile = bookServices.getFile(book);
           if (!bookFile.exists())  {
               Platform.runLater(() -> {
                   Toast.showMessage("《" + book.getName() + "》 的文件不存在");
                   readerView.getView().setDisable(false);
               });
               return;
           }

           Platform.runLater(() -> {


               List<Tab> tabList = tabs.getTabs();
               for (Tab tab: tabList) {
                   if (tab.getUserData() == null) {
                       continue;
                   }
                   BookReader reader = (BookReader) tab.getUserData();
                   if (reader.getBook().getShaCode().equals(book.getShaCode())) {
                       this.tabs.getSelectionModel().select(tab);
                       readerView.getView().setDisable(false);
                       return;
                   }
               }

               try{
                   Toast.showMessage("正在载入《" + book.getTitle() + "》....");
               } catch (Exception e) {
               }

               BookReader reader = desc.createReader(book);

               Tab bookTab = new Tab(book.getTitle());
               Node node = reader.getView();
               node.getStyleClass().add("read-tab");
               bookTab.setContent(node);
               bookTab.setOnClosed(closeEvent -> reader.close());
               bookTab.setClosable(true);
               bookTab.setUserData(reader);

               tabList.add(bookTab);
               this.tabs.getSelectionModel().select(bookTab);
               readerView.getView().setDisable(false);
           });

       });

    }

    @FXML
    public void showAbout() {
        Alert alert = mainView.alert("关于","幻想藏书阁 version 4.0，这是一个" +
                "用于阅读各类电子书的工具，由Fantastic开发，如果有任何问题欢迎和我取得联系。", Alert.AlertType.INFORMATION);
        alert.showAndWait();
    }

    @FXML
    public void showAddType(){
        this.addTypeDialog.show();
    }

    @FXML
    public void syncLibrary() {
        File library = this.resources.getAssetsFolder()
                .toPath()
                .resolve("library")
                .toFile();
        multipleImportDialog.showWithFolder(library);
    }

    @FXML
    public void showOpenFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文档");

        List<FileChooser.ExtensionFilter> filters = bookDescriptors.stream()
                .map(BookDescriptor::getFilter)
                .collect(Collectors.toList());

        List<String> extensions = bookDescriptors
                .stream()
                .map(d -> d.getFilter().getExtensions())
                .flatMap(l -> l.stream())
                .collect(Collectors.toList());

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("所有支持的格式",extensions);

        fileChooser.getExtensionFilters().add(filter);
        fileChooser.getExtensionFilters().addAll(filters);

        File file = fileChooser.showOpenDialog(mainView.getStage());
        if (file == null) {
            return;
        }

        fileDialog.showWithFile(file);
    }

    @FXML
    public void showSearch() {
        this.search.show(this.searchToolButton);
    }

    @FXML
    public void showOpenFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("打开文件夹");
        File folder = directoryChooser.showDialog(mainView.getStage());
        multipleImportDialog.showWithFolder(folder);
    }

    public void showRemoveType() {
        ListView<BookType> typeList = this.typeList;
        BookType type = typeList.getSelectionModel().getSelectedItem();
        if (type == null) {
            return;
        }
        Alert alert = this.mainView.alert("删除","的确要删除 " + type.getName() + " 吗？", Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent((buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                typeServices.remove(type.getId());
                Toast.showMessage("分类已经删除。");
                this.mainView.emit(new TypeListRefreshEvent(""));
                this.mainView.emit(new TreeRefreshEvent(null));
            }
        }));
    }

    public void showEditType() {
        ListView<BookType> typeList = this.typeList;
        BookType type = typeList.getSelectionModel().getSelectedItem();
        if (type == null) {
            return;
        }
        this.editTypeDialog.showWithType(type);
    }


    @EventListener(type = TreeRefreshEvent.class)
    public void treeRefresh(TreeRefreshEvent event) {
        List<TreeItem<DetailTreeData>> authors = this.author.getChildren();
        authors.clear();
        List<String> authorList = this.bookServices.getAuthors();
        for (String authorItem : authorList) {
            authors.add(new TreeItem<>(new DetailTreeData((serv) -> {
                books.clear();
                books.addAll(serv.getByAuthor(authorItem));
            },authorItem,null)));
        }

        List<TreeItem<DetailTreeData>> tags = this.tags.getChildren();
        tags.clear();

        List<BookTag> tagList = tagServices.getTags();
        for (BookTag t: tagList) {
            tags.add(new TreeItem<>(new DetailTreeData((serv) -> {
                books.clear();
                books.addAll(serv.findByTag(t.getId()));
            },t,"name")));
        }

        List<TreeItem<DetailTreeData>> publishers = this.publisher.getChildren();
        publishers.clear();

        List<String> publisherList = bookServices.getPublishers();
        for (String publisherItem : publisherList) {
            publishers.add(new TreeItem<>(new DetailTreeData((serv) -> {
                books.clear();
                books.addAll(serv.getByPublisher(publisherItem));
            },publisherItem,null)));
        }
    }

    @EventListener(type = BookControlCell.BookEditEvent.class)
    public void tableCellEditClicked(BookControlCell.BookEditEvent event) {
        Book target = event.getMessage();
        editDialog.showWithBook(target);
    }

    @EventListener(type = BookControlCell.BookRemoveEvent.class)
    public void tableCellRemoveClicked(BookControlCell.BookRemoveEvent event) {
        Book target = event.getMessage();
        if (target == null) {
            return;
        }
        BookType bookType = target.getType();
        if (bookType == null) {
            return;
        }
        Alert alert = this.readerView.alert("删除","确实要删除《" + target.getName() + "》 吗?", Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent((type) -> {
            if (type.equals(ButtonType.OK)) {
                this.readerView.emit(new BookWillRemoveEvent(target));
                this.bookServices.remove(target.getId());
                this.readerView.emit(new TypeSelectEvent(bookType));
                this.readerView.emit(new TreeRefreshEvent(null));
            }
        });
    }

    /**
     * Type被 增加 或者 删除 的时候，
     * 接收相应的事件并刷新列表。
     * @param event
     */
    @EventListener(type = TypeListRefreshEvent.class)
    public void refreshTypeLists(TypeListRefreshEvent event) {

        BookType select = typeList.getSelectionModel().getSelectedItem();

        List<BookType> types = typeServices.allTypes();
        this.types.clear();
        this.types.addAll(types);

        ListView<BookType> typeList = this.typeList;
        typeList.setItems(this.types);

        this.books.clear();
        if (select != null) {
            BookType type = typeServices.findTypeById(select.getId());
            if(type != null && type.getBooks() != null) {
                this.books.addAll(type.getBooks());
                this.typeList.getSelectionModel().select(type);
            }
        }

    }

    /**
     * 用户选择的Type变化的时候，
     * 刷新Type里面的Book列表。
     * @param event
     */
    @EventListener(type = TypeSelectEvent.class)
    public void typeSelect(TypeSelectEvent event) {
        books.clear();
        resources.getExecutor().execute(() -> {
            BookType type = event.getMessage();
            if (type != null) {
                type = typeServices.findTypeById(type.getId());
                if (type != null) {
                    Set<Book> bookset = type.getBooks();
                    Platform.runLater(() -> books.addAll(bookset));
                }
            }
        });

    }

    @EventListener(type = BookWillRemoveEvent.class)
    public void onBookRemoved(BookWillRemoveEvent event) {
        Book book = event.getMessage();

        List<Tab> tabList = tabs.getTabs();
        for (Tab tab: tabList) {
            if (tab.getUserData() == null) {
                continue;
            }
            BookReader reader = (BookReader)tab.getUserData();
            if (reader.getBook().getShaCode().equals(book.getShaCode())) {
                tabs.getTabs().remove(tab);
                tab.getOnClosed().handle(null);
                return;
            }
        }

    }


}

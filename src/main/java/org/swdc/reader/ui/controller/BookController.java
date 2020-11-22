package org.swdc.reader.ui.controller;

import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.aspect.BookAspect;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.services.index.AbstractIndexer;
import org.swdc.reader.services.index.IndexTreeNode;
import org.swdc.reader.ui.actions.BookCellActions;
import org.swdc.reader.ui.events.*;
import org.swdc.reader.ui.view.MainView;
import org.swdc.reader.ui.view.cells.BookEditCell;
import org.swdc.reader.ui.view.cells.BookEditCellView;
import org.swdc.reader.ui.view.dialogs.BookImportDialog;
import org.swdc.reader.ui.view.dialogs.TypeAddDialog;
import org.swdc.reader.utils.DataUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

    @FXML
    private TreeView<IndexTreeNode> indexTree;

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
        detailTable.setPlaceholder(new Label(this.i18n("lang@table-place-holder")));
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

        BookCellActions actions = findComponent(BookCellActions.class);
        ContextMenu contextMenu = new ContextMenu();
        ObservableList<MenuItem> menuItems = contextMenu.getItems();
        ObservableValue<Book> selectedBook = detailTable.getSelectionModel().selectedItemProperty();
        menuItems.add(DataUtil.createMenuItem(i18n("lang@menu-open"),actions.openBook(selectedBook)));
        menuItems.add(DataUtil.createMenuItem(i18n("lang@menu-contents"),actions.openContentDialog(selectedBook)));
        menuItems.add(DataUtil.createMenuItem(i18n("lang@menu-bookmarks"),actions.openBookMarksDialog(selectedBook)));
        menuItems.add(DataUtil.createMenuItem(i18n("lang@menu-edit"),actions.openEditDialog(selectedBook)));

        detailTable.setContextMenu(contextMenu);

        SimpleBooleanProperty disableMenu = new SimpleBooleanProperty();
        menuItems.forEach(menuItem -> menuItem.disableProperty().bind(disableMenu));

        detailTable.getSelectionModel().selectionModeProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                disableMenu.setValue(true);
                return;
            }
            disableMenu.setValue(false);
        }));

        List<AbstractIndexer> indexers = getScoped(AbstractIndexer.class);
        TreeItem<IndexTreeNode> nodeTreeItem = new TreeItem<>();
        List<TreeItem<IndexTreeNode>> indexTreeNodes = nodeTreeItem.getChildren();

        for (AbstractIndexer indexer: indexers) {
            TreeItem<IndexTreeNode> node = new TreeItem<>(new IndexTreeNode(indexer));
            indexTreeNodes.add(node);

            List<String> keywords = indexer.getKeyWords();
            for (String key: keywords) {
                if (key == null || key.isBlank() || key.isEmpty()) {
                    continue;
                }
                TreeItem<IndexTreeNode> keyNode = new TreeItem<>(new IndexTreeNode(key));
                node.getChildren().add(keyNode);
            }
        }

        nodeTreeItem.setExpanded(true);
        indexTree.setRoot(nodeTreeItem);
        indexTree.setShowRoot(false);

        indexTree.getSelectionModel().selectedItemProperty().addListener(this::onIndexTreeSelect);

        try {
            Image icon = new Image(this.getClass().getModule().getResourceAsStream("appicons/label.png"));

            detailTable.setOnDragDetected(event -> {
                if (detailTable.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                Book book = detailTable.getSelectionModel().getSelectedItem();
                Dragboard dragboard = detailTable.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(book.getId().toString());
                dragboard.setContent(content);
                dragboard.setDragView(icon);
            });
        } catch (Exception e) {
            logger.error("fail to start drag",e);
        }

    }

    private void onIndexTreeSelect(Observable observable, TreeItem<IndexTreeNode> old, TreeItem<IndexTreeNode> next) {
        if (next == null || next.getValue() == null) {
            return;
        }
        IndexTreeNode node = next.getValue();
        typeListView.getSelectionModel().clearSelection();
        if (node.isIndexer()) {
            return;
        }
        IndexTreeNode indexerNode = next.getParent().getValue();
        AbstractIndexer indexer = (AbstractIndexer) findComponent(indexerNode.getIndexerClass());
        List<Book> books = indexer.search(node.getKeywordName());
        this.books.clear();
        this.books.addAll(books);
    }

    private void typeChange(ObservableValue<? extends BookType> typeObservableValue, BookType old, BookType newVal) {
        this.emit(new BooksRefreshEvent(this));
    }

    @Listener(value = BooksRefreshEvent.class,updateUI = true)
    public void onRefreshList(BooksRefreshEvent event) {

        // 切面发送的Event，刷新树的keywords节点
        if (event.getSource().getClass() == BookAspect.class) {
            TreeItem<IndexTreeNode> treeItem = indexTree.getRoot();
            ObservableList<TreeItem<IndexTreeNode>> indexers = treeItem.getChildren();
            for (TreeItem<IndexTreeNode> indexer: indexers) {
                IndexTreeNode node = indexer.getValue();
                if (!node.isIndexer()) {
                    continue;
                }
                indexer.getChildren().clear();
                AbstractIndexer indexerItem = (AbstractIndexer) findComponent(node.getIndexerClass());
                List<String> keywords = indexerItem.getKeyWords();
                for (String key: keywords) {
                    if (key == null || key.isBlank() || key.isEmpty()) {
                        continue;
                    }
                    TreeItem<IndexTreeNode> keyNode = new TreeItem<>(new IndexTreeNode(key));
                    indexer.getChildren().add(keyNode);
                }
            }
        }

        // 刷新books
        BookType type = typeListView.getSelectionModel().getSelectedItem();
        if (type == null) {
            if (indexTree.getSelectionModel() == null) {
                books.clear();
                BookType defaultType = service.getDefaultType();
                Set<Book> books = defaultType.getBooks();
                books = books.stream()
                        .sorted(Comparator.comparingLong(Book::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                this.books.addAll(books);
            }
        } else {
            books.clear();
            this.indexTree.getSelectionModel().clearSelection();
            Set<Book> books = service.getBooks(type).stream()
                    .sorted(Comparator.comparingLong(Book::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            this.books.addAll(books);
            typeListView.getSelectionModel().select(type);
        }
    }


    @Listener(value = TypeRefreshEvent.class,updateUI = true)
    public void onTypeRefresh(TypeRefreshEvent event) {
        List<BookType> types = service.listTypes();
        BookType typeSelected = typeListView.getSelectionModel().getSelectedItem();
        if (typeSelected != null) {
            typeSelected = service.getType(typeSelected.getId());
        }
        bookTypes.clear();
        bookTypes.addAll(types);
        typeListView.getSelectionModel().select(typeSelected);
    }

    @FXML
    public void onAddType() {
        addDialog.show();
    }

    @FXML
    public void onOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(i18n("lang@chooser-open"));
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
    public void onSearch() {
        if (txtSearch.getText().trim().equals("")) {
            return;
        }
        Set<Book> bookSearched = service.searchByName(txtSearch.getText());
        books.clear();
        books.addAll(bookSearched);
    }

}

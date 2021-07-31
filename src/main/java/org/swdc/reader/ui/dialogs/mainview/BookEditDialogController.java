package org.swdc.reader.ui.dialogs.mainview;

import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.StageCloseEvent;
import org.swdc.fx.font.FontawsomeService;
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
import org.swdc.reader.ui.cells.TagFlowCell;
import org.swdc.reader.ui.dialogs.AutoComplete;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class BookEditDialogController implements Initializable {

    @FXML
    private ComboBox<BookType> typeCombo;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAuthor;

    @FXML
    private TextField txtPublisher;

    @FXML
    private TextField tagName;

    @FXML
    private FlowPane tagContainer;

    @Inject
    private BookEditDialog view;

    @Inject
    private TypeServices typeServices;

    @Inject
    private FontawsomeService iconServices;

    @Inject
    private BookServices bookServices;

    @Inject
    private BookTagServices tagServices;

    private Book book;

    private ObservableList<BookType> types = FXCollections.observableArrayList();
    private ObservableList<BookTag> tags = FXCollections.observableArrayList();

    private AutoComplete<BookTag> autoComplete;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.autoComplete = new AutoComplete<>(tagName,"name", (t,input) -> {
            return t.getName().startsWith(input);
        });
        this.autoComplete.bind(tags);
        this.autoComplete.valueProperty().addListener(e -> {
            BookTag tag = autoComplete.getValue();
            if (tag == null) {
                return;
            }
            tagServices.addBook(tag.getName(),book);

            Set<BookTag> tags = this.tagServices.getBookTags(book);
            this.tagContainer.getChildren().clear();
            for (BookTag t: tags) {
                tagContainer.getChildren().add(new TagFlowCell(t,iconServices,view).getView());
            }
        });
    }

    @FXML
    public void onModify() {
        if (book == null) {
            return;
        }
        String name = txtName.getText();
        if (name != null && !name.isEmpty() && !name.equals(book.getName())) {
            book.setName(name);
        }

        String author  = txtAuthor.getText();
        if (author != null && !author.isEmpty() && !author.equals(book.getAuthor())) {
            book.setAuthor(author);
        }

        String publisher = txtPublisher.getText();
        if (publisher != null && !publisher.isEmpty() && !publisher.equals(book.getPublisher())) {
            book.setPublisher(publisher);
        }

        book = bookServices.save(book);

        BookType type = book.getType();
        this.view.emit(new TypeSelectEvent(type));
        this.view.emit(new TreeRefreshEvent(null));
        this.onCancel();
    }

    @FXML
    public void onDelete() {
        if (book == null) {
            return;
        }
        BookType type = book.getType();
        this.view.emit(new BookWillRemoveEvent(book));
        this.bookServices.remove(book.getId());
        this.view.emit(new TypeSelectEvent(type));
        this.view.emit(new TreeRefreshEvent(null));
        this.onCancel();
    }

    @FXML
    public void onCancel() {
        this.view.hide();

    }

    @FXML
    public void setupTags() {
        String name = this.tagName.getText();
        if (book == null) {
            return;
        }
        BookTag tag = tagServices.getTag(name);
        if (tag == null) {
            tag = tagServices.create(name);
        }

        if (tag == null) {
            return;
        }
        tagServices.addBook(tag.getName(),book);

        this.tags.clear();
        this.tags.addAll(this.tagServices.getTags());

        // 初始化book的Tag
        Set<BookTag> tags = this.tagServices.getBookTags(book);
        this.tagContainer.getChildren().clear();
        for (BookTag t: tags) {
            tagContainer.getChildren().add(new TagFlowCell(t,iconServices,view).getView());
        }
        this.view.emit(new TreeRefreshEvent(null));
    }

    public void setBook(Book book) {
        this.book = book;

        this.txtName.setText(book.getTitle());
        this.txtAuthor.setText(book.getAuthor());
        this.txtPublisher.setText(book.getPublisher());
        this.typeCombo.getSelectionModel().select(book.getType());
        // 初始化目前所有的Tag
        this.tags.clear();
        this.tags.addAll(tagServices.getTags());
        // 初始化book的Tag
        Set<BookTag> tags = this.tagServices.getBookTags(book);
        this.tagContainer.getChildren().clear();
        for (BookTag t: tags) {
            tagContainer.getChildren().add(new TagFlowCell(t,iconServices,view).getView());
        }
    }

    @EventListener(type = StageCloseEvent.class)
    public void onStageClose(StageCloseEvent event) {
        if (!event.getMessage().equals(this.view.getClass())) {
            return;
        }
        book = null;
        typeCombo.getSelectionModel().clearSelection();
        txtName.setText("");
        txtAuthor.setText("");
        txtPublisher.setText("");
        this.tags.clear();
    }

    @EventListener(type = TypeListRefreshEvent.class)
    public void refreshTypeLists(TypeListRefreshEvent event) {

        List<BookType> types = typeServices.allTypes();
        this.types.clear();
        this.types.addAll(types);

        typeCombo.setItems(this.types);

    }

    @EventListener(type = TagFlowCell.TagRemoveEvent.class)
    public void onTagRemoveClicked(TagFlowCell.TagRemoveEvent event) {
        BookTag tag = event.getMessage();
        if (tag == null || book == null) {
            return;
        }
        tagServices.removeTag(book,tag);
        // 初始化book的Tag
        Set<BookTag> tags = this.tagServices.getBookTags(book);
        this.tagContainer.getChildren().clear();
        for (BookTag t: tags) {
            tagContainer.getChildren().add(new TagFlowCell(t,iconServices,view).getView());
        }
        this.view.emit(new TreeRefreshEvent(null));
    }

    public void cleanOnClose(){
        this.autoComplete.hide();
    }


}

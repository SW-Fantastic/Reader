package org.swdc.reader.ui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.event.BookLocationEvent;
import org.swdc.reader.core.event.BookProcessEvent;
import org.swdc.reader.core.event.ContentItemFoundEvent;
import org.swdc.reader.core.event.ContentsModeChangeEvent;
import org.swdc.reader.core.readers.AbstractReader;
import org.swdc.reader.core.views.PopupToolView;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.BookService;
import org.swdc.reader.services.CommonComponents;
import org.swdc.reader.ui.events.DocumentOpenEvent;
import org.swdc.reader.ui.view.MainView;
import org.swdc.reader.ui.view.ReadView;
import org.swdc.reader.ui.view.dialogs.ContentsItemView;
import org.swdc.reader.ui.view.dialogs.MarkCreateDialog;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReadController extends FXController {

    private SimpleBooleanProperty disabled = new SimpleBooleanProperty(false);

    @Aware
    private CommonComponents commonComponents = null;

    @Aware
    private BookService service = null;

    @Aware
    private MarkCreateDialog markCreateDialog = null;

    @Aware
    private ContentsItemView contentsItemView = null;

    private AbstractReader currentReader = null;

    private PopupToolView toolView;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtLocation;

    @FXML
    private ProgressBar progress;

    @FXML
    private Label procMessage;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button addLblButton;

    @FXML
    private Button jumpButton;

    @FXML
    private Button contentsButton;

    @FXML
    private Button floatTools;

    private final Object lock = new Object();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        procMessage.setText("");
        addLblButton.disableProperty().bind(disabled);
        contentsButton.disableProperty().bind(disabled);
        prevButton.disableProperty().bind(disabled);
        nextButton.disableProperty().bind(disabled);
        jumpButton.disableProperty().bind(disabled);
        floatTools.disableProperty().bind(disabled);
        disabled.set(true);
    }

    @Listener(ContentItemFoundEvent.class)
    public void contentItemFound(ContentItemFoundEvent event) {
        service.createContentItems(event.getData());
    }

    @Listener(DocumentOpenEvent.class)
    public void bookOpenRequested(DocumentOpenEvent event) {
        List<AbstractReader> readers = getScoped(AbstractReader.class);
        ReadView view = getView();
        commonComponents.submitTask(() ->{
            synchronized (lock) {
                readers.stream().filter(reader -> reader.isSupport(event.getData()))
                        .findFirst().ifPresent((AbstractReader reader) -> {
                    if (currentReader != null) {
                        if (event.getData().getId().longValue() == currentReader.getBook().getId().longValue()) {
                            return;
                        } else if (currentReader != null && currentReader != reader) {
                            currentReader.finalizeResources();
                        }
                    }
                    Platform.runLater(() -> {
                        view.showToast(this.i18n("lang@text-loading") + event.getData().getTitle());
                    });
                    this.currentReader = reader;
                    reader.setBook(event.getData());
                    BookLocator locator = reader.getLocator();
                    Platform.runLater(() -> {
                        reader.renderPage(locator.nextPage(), view.getView());
                        txtTitle.setText(locator.getTitle());
                        txtLocation.setText(locator.getLocation());
                        disabled.set(false);
                    });
                });
            }
        });
    }

    @Listener(BookLocationEvent.class)
    public void onLocationRequest(BookLocationEvent event) {
        ReadView view = getView();
        disabled.set(true);
        commonComponents.submitTask(() -> {
            synchronized (lock){
                try {
                    if (currentReader == null) {
                        return;
                    }
                    String location = event.getData();
                    BookLocator locator = currentReader.getLocator();
                    Object data = locator.toPage(location);
                    Platform.runLater(() -> {
                        currentReader.renderPage(data, view.getView());
                        txtTitle.setText(locator.getTitle());
                        txtLocation.setText(locator.getLocation());
                        disabled.set(false);
                    });
                } catch (Exception e) {
                    this.emit(event);
                }
            }
        });
    }

    @Listener(value = BookProcessEvent.class,updateUI = true)
    public void processEvent(BookProcessEvent event) {
        progress.setProgress(event.getData());
        procMessage.setText(event.getMessage());
        synchronized (lock) {
            if (event.getData() < 1) {
                disabled.set(true);
            } else {
                disabled.set(false);
            }
        }
    }

    @FXML
    public void onPrevPage() {
        ReadView view = getView();
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        BookLocator locator = currentReader.getLocator();
        Object data = locator.prevPage();
        currentReader.renderPage(data, view.getView());
        txtTitle.setText(locator.getTitle());
        txtLocation.setText(locator.getLocation());
    }

    @FXML
    public void onNextPage() {
        ReadView view = getView();
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        BookLocator locator = currentReader.getLocator();
        Object data = locator.nextPage();
        currentReader.renderPage(data, view.getView());
        txtTitle.setText(locator.getTitle());
        txtLocation.setText(locator.getLocation());
    }

    @FXML
    public void onAddMarks() {
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        markCreateDialog.setBook(currentReader);
        markCreateDialog.show();
    }

    @FXML
    public void onPageTo() {
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        this.emit(new BookLocationEvent(txtLocation.getText(),this));
    }

    @FXML
    public void onContentsItem() {
        contentsItemView.show();
    }

    public AbstractReader getCurrentReader() {
        return currentReader;
    }

    public void closeOpenedBook() {
        if (currentReader == null) {
            return;
        }
        if (currentReader.getBook() == null) {
            return;
        }
        currentReader.getLocator().finalizeResources();
        currentReader = null;
        disabled.set(true);
    }

    @FXML
    public void toggleFloatTools () {
        if(toolView == null) {
            MainView view = findView(MainView.class);
            toolView = new PopupToolView(view.getStage());
        }
        if (currentReader == null) {
            toolView.hide();
            return;
        }
        Node toolsView = currentReader.getView().getToolsView();
        if (toolsView == null) {
            toolView.hide();
            return;
        }
        if (!toolsView.disableProperty().isBound()) {
            toolsView.disableProperty().bind(disabled);
        }
        if (disabled.getValue()) {
            return;
        }
        toolView.setGraph(toolsView);
        if (toolView.isShowing()) {
            toolView.hide();
        } else {
            toolView.showPopup();
        }
    }

}

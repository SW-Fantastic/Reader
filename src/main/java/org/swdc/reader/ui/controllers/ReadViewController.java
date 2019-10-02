package org.swdc.reader.ui.controllers;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import lombok.Getter;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BookLocationEvent;
import org.swdc.reader.event.BookProcessEvent;
import org.swdc.reader.event.ContentItemFoundEvent;
import org.swdc.reader.event.DocumentOpenEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.ReadView;
import org.swdc.reader.ui.views.dialog.ContentsItemView;
import org.swdc.reader.ui.views.dialog.MarkAddDialog;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Created by lenovo on 2019/5/31.
 */
@FXMLController
public class ReadViewController implements Initializable {

    @Autowired
    @Qualifier("asyncExecutor")
    private Executor executor;

    @Autowired
    private ReadView view;

    @Autowired
    private List<BookReader<?>> readers;

    @Autowired
    private BookService service;

    @Autowired
    private ContentsItemView contentsItemView;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private MarkAddDialog markAddDialog;

    private BookReader currentReader;

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

    private SimpleBooleanProperty disabled = new SimpleBooleanProperty(false);

    private final Object lock = new Object();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        procMessage.setText("");
        addLblButton.disableProperty().bind(disabled);
        contentsButton.disableProperty().bind(disabled);
        prevButton.disableProperty().bind(disabled);
        nextButton.disableProperty().bind(disabled);
        jumpButton.disableProperty().bind(disabled);
    }

    @EventListener(ContentItemFoundEvent.class)
    public void contentItemFound(ContentItemFoundEvent event) {
        service.createContentItems(event.getSource());
    }

    @EventListener(BookLocationEvent.class)
    public void onLocationRequest(BookLocationEvent event) {
        executor.execute(() -> {
            synchronized (lock){
                try {
                    if (currentReader == null) {
                        return;
                    }
                    String location = event.getSource();
                    BookLocator locator = currentReader.getLocator();
                    Object data = locator.toPage(location);
                    Platform.runLater(() -> {
                        currentReader.renderPage(data, (BorderPane)view.getView());
                        txtTitle.setText(locator.getTitle());
                        txtLocation.setText(locator.getLocation());
                    });
                } catch (Exception e) {
                    config.publishEvent(event);
                }
            }
        });
    }

    @EventListener(DocumentOpenEvent.class)
    public void bookOpenRequested(DocumentOpenEvent event) {
        if (this.disabled.get()) {
            return;
        }
        executor.execute(() ->{
            synchronized (lock) {
                readers.stream().filter(reader -> reader.isSupport(event.getSource()))
                        .findFirst().ifPresent((BookReader reader) -> {
                    if (currentReader != null) {
                        if (event.getSource().getId().longValue() == currentReader.getBook().getId().longValue()) {
                            return;
                        } else if (currentReader != null && currentReader != reader) {
                            currentReader.finalizeResources();
                        }
                    }
                    Platform.runLater(() -> {
                        Notifications.create()
                                .owner(GUIState.getStage())
                                .hideCloseButton()
                                .hideAfter(Duration.millis(2000))
                                .text("正在载入《" + event.getSource().getTitle() + "》")
                                .position(Pos.CENTER)
                                .show();
                    });
                    this.currentReader = reader;
                    reader.setBook(event.getSource());
                    BookLocator locator = reader.getLocator();
                    Platform.runLater(() -> {
                        reader.renderPage(locator.nextPage(), (BorderPane) view.getView());
                        txtTitle.setText(locator.getTitle());
                        txtLocation.setText(locator.getLocation());
                    });
                });
            }
        });
    }

    @EventListener(BookProcessEvent.class)
    public void processEvent(BookProcessEvent event) {
        Platform.runLater(() -> {
            progress.setProgress(event.getSource());
            procMessage.setText(event.getMessage());
            if (event.getSource() < 1) {
                disabled.set(true);
            } else {
                disabled.set(false);
            }
        });
    }

    @FXML
    public void onPrevPage() {
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        BookLocator locator = currentReader.getLocator();
        Object data = locator.prevPage();
        currentReader.renderPage(data, (BorderPane) view.getView());
        txtTitle.setText(locator.getTitle());
        txtLocation.setText(locator.getLocation());
    }

    @FXML
    public void onContentsItem() {
        contentsItemView.show();
    }

    @FXML
    public void onPageTo(){
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        config.publishEvent(new BookLocationEvent(txtLocation.getText()));
    }

    @FXML
    public void onAddMarks() {
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        markAddDialog.setBook(currentReader);
        markAddDialog.show();
    }

    @FXML
    public void onNextPage() {
        if (currentReader == null || this.disabled.get()) {
            return;
        }
        BookLocator locator = currentReader.getLocator();
        Object data = locator.nextPage();
        currentReader.renderPage(data, (BorderPane) view.getView());
        txtTitle.setText(locator.getTitle());
        txtLocation.setText(locator.getLocation());
    }

}

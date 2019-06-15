package org.swdc.reader.ui.controllers;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BookLocationEvent;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener(ContentItemFoundEvent.class)
    public void contentItemFound(ContentItemFoundEvent event) {
        service.createContentItems(event.getSource());
    }

    @EventListener(BookLocationEvent.class)
    public void onLocationRequest(BookLocationEvent event) {
        Platform.runLater(() -> {
            if (currentReader == null) {
                return;
            }
            String location = event.getSource();
            BookLocator locator = currentReader.getLocator();
            Object data = locator.toPage(location);
            currentReader.renderPage(data, (BorderPane)view.getView());
            txtTitle.setText(locator.getTitle());
            txtLocation.setText(locator.getLocation());
        });
    }

    @EventListener(DocumentOpenEvent.class)
    public void bookOpenRequested(DocumentOpenEvent event) {
        executor.execute(() ->
            readers.stream().filter(reader -> reader.isSupport(event.getSource()))
                    .findFirst().ifPresent((BookReader reader) -> {
                if (currentReader != null) {
                    if (event.getSource().getId().longValue() == currentReader.getBook().getId().longValue()) {
                        return;
                    } else if (currentReader != null && currentReader != reader) {
                        currentReader.finalizeResources();
                    }
                }
                this.currentReader = reader;
                reader.setBook(event.getSource());
                BookLocator locator = reader.getLocator();
                Platform.runLater(() -> {
                    reader.renderPage(locator.nextPage(), (BorderPane) view.getView());
                    txtTitle.setText(locator.getTitle());
                    txtLocation.setText(locator.getLocation());
                });
            })
        );
    }

    @FXML
    public void onPrevPage() {
        if (currentReader == null) {
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
        if (currentReader == null) {
            return;
        }
        config.publishEvent(new BookLocationEvent(txtLocation.getText()));
    }

    @FXML
    public void onAddMarks() {
        markAddDialog.setBook(currentReader);
        markAddDialog.show();
    }

    @FXML
    public void onNextPage() {
        if (currentReader == null) {
            return;
        }
        BookLocator locator = currentReader.getLocator();
        Object data = locator.nextPage();
        currentReader.renderPage(data, (BorderPane) view.getView());
        txtTitle.setText(locator.getTitle());
        txtLocation.setText(locator.getLocation());
    }

}

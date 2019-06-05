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
import org.springframework.context.event.EventListener;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.event.ContentItemFoundEvent;
import org.swdc.reader.event.DocumentOpenEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.views.ReadView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/5/31.
 */
@FXMLController
public class ReadViewController implements Initializable {

    @Autowired
    private ReadView view;

    @Autowired
    private List<BookReader<?>> readers;

    @Autowired
    private BookService service;

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

    @EventListener(DocumentOpenEvent.class)
    public void bookOpenRequested(DocumentOpenEvent event) {
        if (this.currentReader != null) {
            currentReader = null;
        }
        readers.stream().filter(reader -> reader.isSupport(event.getSource()))
                .findFirst().ifPresent((BookReader reader) -> {
            if (currentReader != null && currentReader != reader) {
                currentReader.finalizeResources();
            }
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

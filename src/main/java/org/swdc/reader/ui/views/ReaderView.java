package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.event.ViewChangeEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import static org.swdc.reader.utils.UIUtils.findById;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/19.
 */
@FXMLView("/views/ReaderView.fxml")
public class ReaderView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private BooksView booksView;

    @Autowired
    private ReadView readView;

    private ToggleGroup group = new ToggleGroup();

    private ObservableList<Node> toolBarButtons;

    @PostConstruct
    protected void initUI() throws Exception {
        BorderPane pane = (BorderPane)this.getView();
        UIUtils.configUI(pane, config);
        ToolBar toolBar = (ToolBar)pane.lookup("#tools");
        this.toolBarButtons = toolBar.getItems();
        this.initTools();

        pane.setCenter(booksView.getView());

        this.initWidthHeight();
        pane.setStyle(pane.getStyle() + "-fx-background-image:url("  + ApplicationConfig.getConfigLocation() + "images/" + config.getBackground() + ");");
    }

    @EventListener(ViewChangeEvent.class)
    protected void onViewChange(ViewChangeEvent event) {
        BorderPane pane = (BorderPane)this.getView();
        switch (event.getSource()) {
            case "books":
                pane.setCenter(booksView.getView());
                group.selectToggle(findById("books", toolBarButtons));
                break;
            case "read":
                pane.setCenter(readView.getView());
                group.selectToggle(findById("read", toolBarButtons));
                break;
        }
    }

    private void initTools() {
        this.initToggleBtn(findById("books", toolBarButtons), "book", this::onBooksChange);
        this.initToggleBtn(findById("read", toolBarButtons), "sticky_note", this::onReadChange);
        this.initToggleBtn(findById("conf", toolBarButtons), "cog", this::onConfigChange);
        this.group.selectedToggleProperty().addListener(this::onSelectionChange);
        ToggleButton books = findById("books", toolBarButtons);
        books.setSelected(true);
    }

    private void initWidthHeight(){
        BorderPane pane = (BorderPane) this.getView();
        pane.widthProperty().addListener(this::onWidthChange);
        pane.heightProperty().addListener(this::onHeightChange);
        ToolBar toolBar = (ToolBar) this.getView().lookup("#tools");

        BorderPane bookPanel = (BorderPane)booksView.getView();
        bookPanel.setPrefWidth(pane.getPrefWidth() - toolBar.getPrefWidth() - 12);
        bookPanel.setPrefHeight(pane.getPrefHeight() - 8);

        BorderPane readPane = (BorderPane)readView.getView();
        readPane.setPrefWidth(pane.getPrefWidth() - toolBar.getPrefWidth() - 12);
        readPane.setPrefHeight(pane.getPrefHeight() - 8);
    }

    private void initToggleBtn(ToggleButton button, String name, ChangeListener<Boolean> onChange) {
        button.setText("" + AwsomeIconData.getAwesomeMap().get(name));
        button.setFont(AwsomeIconData.getFontIcon());
        button.selectedProperty().addListener(onChange);
        group.getToggles().add(button);
    }

    private void onBooksChange(ObservableValue<? extends Boolean> observableValue,Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            config.publishEvent(new ViewChangeEvent("books"));
        }
    }

    private void onReadChange(ObservableValue<? extends Boolean> observableValue,Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            config.publishEvent(new ViewChangeEvent("read"));
        }
    }

    private void onConfigChange(ObservableValue<? extends Boolean> observableValue,Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            config.publishEvent(new ViewChangeEvent("config"));
        }
    }

    private void onSelectionChange(ObservableValue<? extends Toggle> observable, Toggle oldVal, Toggle newVal) {
        if (newVal == null) {
            oldVal.setSelected(true);
        }
    }

    private void onWidthChange(ObservableValue<? extends  Number> observable, Number oldVal, Number newVal) {
        ToolBar toolBar = (ToolBar) this.getView().lookup("#tools");
        BorderPane pane = (BorderPane) this.getView();

        BorderPane bookPane = (BorderPane)booksView.getView();
        bookPane.setPrefWidth(pane.getWidth() - toolBar.getPrefWidth() - 12);

        BorderPane readPane = (BorderPane)readView.getView();
        readPane.setPrefWidth(pane.getWidth() - toolBar.getPrefWidth() - 12);
    }

    private void onHeightChange(ObservableValue<? extends  Number> observable, Number oldVal, Number newVal) {
        BorderPane pane = (BorderPane) this.getView();

        BorderPane bookPanel = (BorderPane)booksView.getView();
        bookPanel.setPrefHeight(pane.getHeight() + 10);

        BorderPane readPane = (BorderPane)readView.getView();
        readPane.setPrefHeight(pane.getHeight() + 10);
    }

}

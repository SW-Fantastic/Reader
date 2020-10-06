package org.swdc.reader.ui.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.ui.events.ViewChangeEvent;

@View(background = true,title = "lang@window-title",resizeable = true)
public class MainView extends FXView {

    @Aware
    private BooksView booksView = null;

    @Aware
    private ReadView readView = null;

    @Aware
    private ConfigView configView = null;

    @Aware
    private FontawsomeService fontawsomeService = null;

    private ToggleGroup group = new ToggleGroup();

    private SimpleBooleanProperty silderFocusable = new SimpleBooleanProperty(true);

    @Override
    public void initialize() {
        Stage stage = getStage();
        stage.setMinHeight(680);
        stage.setMinWidth(1000);

        stage.getScene().setOnKeyPressed(e -> {
            ToggleButton selected = (ToggleButton)group.getSelectedToggle();
            if (selected.getId().equals("read")) {
                silderFocusable.set(false);
                if (e.getCode() == KeyCode.RIGHT) {
                    readView.nextPage();
                } else if (e.getCode() == KeyCode.LEFT) {
                    readView.prevPage();
                } else if (e.getCode() == KeyCode.ENTER) {
                    readView.focus();
                } else if (e.getCode() == KeyCode.TAB) {
                    silderFocusable.set(!silderFocusable.get());
                } else if (e.isAltDown()) {
                    readView.toggleFloatTools();
                }
            } else {
                silderFocusable.set(true);
            }
        });

        this.initTools();
        BorderPane pane = this.getView();
        pane.setCenter(booksView.getView());
        this.initWidthHeight();
    }

    private void initWidthHeight(){
        BorderPane pane = this.getView();
        pane.widthProperty().addListener(this::onWidthChange);
        pane.heightProperty().addListener(this::onHeightChange);
        ToolBar toolBar = this.findById("tools");

        BorderPane bookPanel = booksView.getView();
        bookPanel.setPrefWidth(pane.getPrefWidth() - toolBar.getPrefWidth() - 12);
        bookPanel.setPrefHeight(pane.getPrefHeight() - 8);

        BorderPane readPane = readView.getView();
        readPane.setPrefWidth(pane.getPrefWidth() - toolBar.getPrefWidth() - 12);
        readPane.setPrefHeight(pane.getPrefHeight() - 8);

        BorderPane configPane = configView.getView();
        configPane.setPrefWidth(pane.getPrefWidth() - toolBar.getPrefWidth() - 12);
        configPane.setPrefHeight(pane.getPrefHeight() - 8);
    }

    private void initTools() {
        this.initToggleBtn(findById("books"), "book", this::onBooksChange);
        this.initToggleBtn(findById("read"), "sticky_note", this::onReadChange);
        this.initToggleBtn(findById("conf"), "cog", this::onConfigChange);
        this.group.selectedToggleProperty().addListener(this::onSelectionChange);
        ToggleButton books = findById("books");
        books.setSelected(true);
    }

    private void initToggleBtn(ToggleButton button, String name, ChangeListener<Boolean> onChange) {
        button.setText(fontawsomeService.getFontIcon(name));
        button.setFont(fontawsomeService.getFont(FontSize.MIDDLE_SMALL));
        button.selectedProperty().addListener(onChange);
        button.focusTraversableProperty().bind(silderFocusable);
        group.getToggles().add(button);
    }

    private void onBooksChange(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            this.emit(new ViewChangeEvent("books",this));
        }
    }

    private void onReadChange(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            this.emit(new ViewChangeEvent("read",this));
        }
    }

    private void onConfigChange(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
        if (newVal != null && newVal) {
            this.emit(new ViewChangeEvent("config",this));
        }
    }

    private void onSelectionChange(ObservableValue<? extends Toggle> observable, Toggle oldVal, Toggle newVal) {
        if (newVal == null) {
            oldVal.setSelected(true);
        }
    }

    private void onWidthChange(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
        ToolBar toolBar = this.findById("tools");
        BorderPane pane = this.getView();

        BorderPane bookPane = booksView.getView();
        bookPane.setPrefWidth(pane.getWidth() - toolBar.getPrefWidth() - 12);

        BorderPane readPane = readView.getView();
        readPane.setPrefWidth(pane.getWidth() - toolBar.getPrefWidth() - 12);

        BorderPane configPane = configView.getView();
        configPane.setPrefWidth(pane.getWidth() - toolBar.getPrefWidth() - 12);
    }

    private void onHeightChange(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
        BorderPane pane = this.getView();

        BorderPane bookPanel = booksView.getView();
        bookPanel.setPrefHeight(pane.getHeight() + 10);

        BorderPane readPane = readView.getView();
        readPane.setPrefHeight(pane.getHeight() + 10);

        BorderPane configPane = configView.getView();
        configPane.setPrefHeight(pane.getHeight() + 10);
    }

    @Listener(ViewChangeEvent.class)
    public void onViewChange(ViewChangeEvent event) {
        BorderPane pane = this.getView();
        switch (event.getName()) {
            case "books":
                pane.setCenter(booksView.getView());
                group.selectToggle(findById("books"));
                break;
            case "read":
                pane.setCenter(readView.getView());
                group.selectToggle(findById("read"));
                break;
            case "config":
                pane.setCenter(configView.getView());
                group.selectToggle(findById("conf"));
                break;
        }
    }

}

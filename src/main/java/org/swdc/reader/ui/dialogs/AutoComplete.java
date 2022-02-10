package org.swdc.reader.ui.dialogs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.swdc.reader.ui.cells.PropertyListCell;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AutoComplete<T> {

    private PopOver popOver;
    private ListView<T> autoList;
    private ObservableList<T> items = FXCollections.observableArrayList();
    private ObservableList<T> filtered = FXCollections.observableArrayList();
    private SimpleObjectProperty<T> value =  new SimpleObjectProperty<>();
    private TextField field;

    private Function<T,String> converter;
    private Predicate<T> filter;
    private Stage stage;

    public AutoComplete(TextField field, String propertyName, BiPredicate<T,String> filter) {
        this.field = field;

        this.popOver = new PopOver();
        this.popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        this.autoList = new ListView<>();
        this.autoList.setItems(filtered);
        this.autoList.setPrefHeight(240);
        this.autoList.setCellFactory(l -> new PropertyListCell<>(propertyName));

        BorderPane pane = new BorderPane();
        pane.setCenter(autoList);
        this.popOver.setContentNode(pane);
        this.value.bind(this.autoList.getSelectionModel().selectedItemProperty());

        field.textProperty().addListener(e ->{
            this.filtered.clear();
            List<T> filtered =  this.items.stream()
                    .filter(s -> filter.test(s,field.getText()))
                    .collect(Collectors.toList());
            this.filtered.addAll(filtered);
            Bounds boundsInLocal = field.getBoundsInLocal();
            Bounds screenBounds = field.localToScreen(boundsInLocal);
            this.popOver.show(this.field,screenBounds.getMinX() + screenBounds.getWidth() / 2.0,screenBounds.getMaxY());
        });

        this.autoList.setOnMouseClicked(e -> {
            field.setText("");
            this.hide();
        });

        this.autoList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER ||
                    e.getCode() == KeyCode.BACK_SPACE ||
                    e.getCode() == KeyCode.SPACE) {
                field.setText("");
                this.hide();
            }
        });
    }

    public void hide() {
        this.popOver.hide(Duration.ZERO);
        field.setText("");
    }

    public void bind(ObservableList<T> list) {
        this.items = list;
    }

    public ObservableObjectValue<T> valueProperty() {
        return value;
    }

    public T getValue() {
        return this.value.getValue();
    }

}

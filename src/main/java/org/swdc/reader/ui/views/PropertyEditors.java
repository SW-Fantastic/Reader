package org.swdc.reader.ui.views;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.property.editor.PropertyEditor;
import org.swdc.reader.anno.ConfigProp;
import org.swdc.reader.core.ConfigProperty;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.AwsomeIconData;

import java.io.File;

/**
 * Created by lenovo on 2019/6/8.
 */
public class PropertyEditors {

    public static PropertyEditor<?> createFileImportableEditor(ConfigProperty property) {
        ConfigProp propData = property.getPropData();
        ObservableList<String> files = FXCollections.observableArrayList();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(files);
        comboBox.setPrefHeight(28);
        HBox.setHgrow(comboBox, Priority.ALWAYS);

        Button buttonImport = new Button();
        buttonImport.setFont(AwsomeIconData.getFontIconSmall());
        buttonImport.setText(AwsomeIconData.getAwesomeMap().get("folder_open") + "");

        files.clear();
        File folder = new File(propData.value());
        for(File file : folder.listFiles()) {
            files.add(file.getName());
        }

        buttonImport.setOnAction(e -> {
        });

        hBox.getChildren().addAll(comboBox, buttonImport);
        hBox.widthProperty().addListener(((observable, oldValue, newValue) -> {
            comboBox.setPrefWidth(hBox.getWidth() - buttonImport.getWidth());
        }));
        return new PropertyEditor() {

            @Override
            public Node getEditor() {
                return hBox;
            }

            @Override
            public Object getValue() {
                return comboBox.getSelectionModel().getSelectedItem();
            }

            @Override
            public void setValue(Object o) {
                comboBox.getSelectionModel().select(o.toString());
            }
        };
    }

    public static PropertyEditor<?> createFolderImportableEditor(ConfigProperty property) {
        ConfigProp propData = property.getPropData();
        ObservableList<String> files = FXCollections.observableArrayList();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(files);
        comboBox.setPrefHeight(28);
        HBox.setHgrow(comboBox, Priority.ALWAYS);

        Button buttonImport = new Button();
        buttonImport.setFont(AwsomeIconData.getFontIconSmall());
        buttonImport.setText(AwsomeIconData.getAwesomeMap().get("folder_open") + "");

        files.clear();
        File folder = new File(propData.value());
        for(File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.add(file.getName());
            }
        }

        buttonImport.setOnAction(e -> {
        });

        hBox.getChildren().addAll(comboBox, buttonImport);
        hBox.widthProperty().addListener(((observable, oldValue, newValue) -> {
            comboBox.setPrefWidth(hBox.getWidth() - buttonImport.getWidth());
        }));
        return new PropertyEditor() {

            @Override
            public Node getEditor() {
                return hBox;
            }

            @Override
            public Object getValue() {
                return comboBox.getSelectionModel().getSelectedItem();
            }

            @Override
            public void setValue(Object o) {
                comboBox.getSelectionModel().select(o.toString());
            }
        };
    }

    public static PropertyEditor<?> createCheckedEditor(ConfigProperty property) {
        CheckBox check = new CheckBox();
        check.setSelected((Boolean)property.getValue());
        check.selectedProperty().addListener((observable, oldValue, newValue) -> {
            property.setValue(newValue);
        });
        check.setPrefHeight(28);
        return new PropertyEditor() {
            @Override
            public Node getEditor() {
                return check;
            }

            @Override
            public Object getValue() {
                return check.isSelected();
            }

            @Override
            public void setValue(Object o) {
                check.setSelected((Boolean)o);
            }
        };
    }

    public static PropertyEditor<?> createColorEditor(ConfigProperty property){
        HBox hbox = new HBox();
        ColorPicker picker = new ColorPicker();
        TextField text = new TextField();

        HBox.setHgrow(picker, Priority.ALWAYS);
        HBox.setHgrow(text, Priority.ALWAYS);
        hbox.getChildren().addAll(picker, text);

        picker.setValue(Color.web(property.getValue().toString()));
        text.setText(property.getValue().toString());

        picker.setPrefHeight(28);
        text.setPrefHeight(28);

        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            picker.setValue(newValue);
            String data = "#" + Integer.toHexString(newValue.hashCode()).substring(0,6);
            text.setText(data);
            property.setValue(data);
        });

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 6) {
                picker.setValue(Color.web(newValue));
                property.setValue(newValue);
            }
        });

        return new PropertyEditor() {
            @Override
            public Node getEditor() {
                return hbox;
            }

            @Override
            public Object getValue() {
                return text.getText();
            }

            @Override
            public void setValue(Object o) {
                if (o instanceof Color) {
                    String data = "#" + Integer.toHexString(o.hashCode()).substring(0,6);
                    text.setText(data);
                    picker.setValue(Color.web(data));
                } else {
                    text.setText(o.toString());
                    picker.setValue(Color.web(o.toString()));
                }
            }
        };
    }

    public static PropertyEditor<?> createNumberRangeEditor(ConfigProperty property) {
        ConfigProp prop = property.getPropData();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        Slider slider = new Slider();
        slider.setMax(Double.valueOf(prop.value()));
        slider.setValue(Double.valueOf(property.getValue().toString()));
        slider.setPrefHeight(28);
        slider.setMin(18);

        HBox.setHgrow(slider, Priority.ALWAYS);

        TextField text = new TextField();
        text.setText(property.getValue().toString());
        text.setPrefHeight(28);
        text.setPrefWidth(80);

        hBox.getChildren().addAll(slider, text);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            text.setText(newValue.intValue() + "");
            property.setValue(newValue.intValue());
        });

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().equals("")) {
                return;
            }
            slider.setValue(Double.valueOf(newValue));
            property.setValue(Double.valueOf(newValue).intValue());
        });

        return new PropertyEditor() {
            @Override
            public Node getEditor() {
                return hBox;
            }

            @Override
            public Object getValue() {
                return slider.getValue();
            }

            @Override
            public void setValue(Object o) {
                slider.setValue(Double.valueOf(o.toString()));
                text.setText(o.toString());
            }
        };
    }

}

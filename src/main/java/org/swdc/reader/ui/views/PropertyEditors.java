package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.GUIState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.util.FileUtil;
import org.controlsfx.property.editor.PropertyEditor;
import org.swdc.reader.anno.ConfigProp;
import org.swdc.reader.core.ConfigProperty;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by lenovo on 2019/6/8.
 */
@CommonsLog
public class PropertyEditors {

    public static PropertyEditor<?> createFileImportableEditor(ConfigProperty property, ApplicationConfig config) {
        ConfigProp propData = property.getPropData();
        ObservableList<String> files = FXCollections.observableArrayList();

        HBox hBox = new HBox();
        hBox.setSpacing(8);
        hBox.setAlignment(Pos.CENTER);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(files);
        comboBox.setPrefHeight(28);
        comboBox.getStyleClass().add("comb");
        HBox.setHgrow(comboBox, Priority.ALWAYS);

        Button buttonImport = new Button();
        buttonImport.getStyleClass().add("btn");
        buttonImport.setFont(AwsomeIconData.getFontIconSmall());
        buttonImport.setText(AwsomeIconData.getAwesomeMap().get("folder_open") + "");

        files.clear();
        File folder = new File(propData.value());
        for(File file : folder.listFiles()) {
            if (file.isFile()) {
                files.add(file.getName());
            }
        }

        buttonImport.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("导入");
            File target = chooser.showOpenDialog(GUIState.getStage());
            if (target == null || !target.exists()) {
                return;
            }
            try {
                FileUtil.copyFile(target, new File(folder.getPath() + "/" + target.getName()));
                files.clear();
                for(File file : folder.listFiles()) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            } catch (IOException ex) {
                log.error(ex);
                UIUtils.showAlertDialog("导入资源失败", "提示", Alert.AlertType.ERROR, config);
            }
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
        hBox.setSpacing(8);
        hBox.setAlignment(Pos.CENTER);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(files);
        comboBox.setPrefHeight(28);
        comboBox.getStyleClass().add("comb");
        HBox.setHgrow(comboBox, Priority.ALWAYS);

        Button buttonImport = new Button();
        buttonImport.getStyleClass().add("btn");
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
        check.getStyleClass().add("check");
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
        hbox.setSpacing(8);
        ColorPicker picker = new ColorPicker();
        TextField text = new TextField();

        HBox.setHgrow(picker, Priority.ALWAYS);
        HBox.setHgrow(text, Priority.ALWAYS);
        hbox.getChildren().addAll(picker, text);

        picker.setValue(Color.web(property.getValue().toString()));
        text.setText(property.getValue().toString());

        picker.setPrefHeight(28);
        text.setPrefHeight(28);
        text.getStyleClass().add("txt");

        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            picker.setValue(newValue);
            String data = "#" + Integer.toHexString(newValue.hashCode()).substring(0,6);
            text.setText(data);
            property.setValue(data);
        });

        picker.getStyleClass().add("comb");

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
        hBox.setSpacing(8);
        hBox.setAlignment(Pos.CENTER);

        Slider slider = new Slider();
        slider.setMax(Double.valueOf(prop.value()));
        slider.setValue(Double.valueOf(property.getValue().toString()));
        slider.setPrefHeight(28);
        slider.setMin(18);

        HBox.setHgrow(slider, Priority.ALWAYS);

        TextField text = new TextField();
        text.getStyleClass().add("txt");
        text.setText(property.getValue().toString());
        text.setPrefHeight(28);
        text.setPrefWidth(80);

        hBox.getChildren().addAll(slider, text);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (property.getType() == Integer.class || property.getType() == int.class) {
                text.setText(newValue.intValue() + "");
                property.setValue(newValue.intValue());
            } else if (property.getType() == Float.class || property.getType() == float.class){
                text.setText(newValue.floatValue() + "");
                property.setValue(newValue.floatValue());
            } else if (property.getType() == Double.class || property.getType() == double.class) {
                text.setText(newValue.doubleValue() +  "");
                property.setValue(newValue.doubleValue());
            }
        });

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.trim().equals("")) {
                    return;
                }
                slider.setValue(Double.valueOf(newValue));
                if (property.getType() == Integer.class || property.getType() == int.class) {
                    property.setValue(Integer.valueOf(newValue));
                } else if (property.getType() == Float.class || property.getType() == float.class){
                    property.setValue(Float.valueOf(newValue));
                } else if (property.getType() == Double.class || property.getType() == double.class) {
                    property.setValue(Double.valueOf(newValue));
                }
            } catch (Exception e) {
                log.error(e);
            }
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

    public static PropertyEditor<?> createNumberEditor(ConfigProperty property) {
        TextField textField = new TextField();
        textField.getStyleClass().add("txt");
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (textField.getText().trim().equals("")) {
                return;
            }
            try {
                if (property.getType() == Integer.class || property.getType() == int.class) {
                    property.setValue(Integer.valueOf(newValue));
                } else if (property.getType() == Float.class || property.getType() == float.class){
                    property.setValue(Float.valueOf(newValue));
                } else if (property.getType() == Double.class || property.getType() == double.class) {
                    property.setValue(Double.valueOf(newValue));
                }
            } catch (Exception ex){
                textField.setText("");
            }
        }));
        return new PropertyEditor() {
            @Override
            public Node getEditor() {
                return textField;
            }

            @Override
            public Object getValue() {
                return textField.getText();
            }

            @Override
            public void setValue(Object o) {
                textField.setText(o.toString());
            }
        };
    }

}

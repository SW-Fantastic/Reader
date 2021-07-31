package org.swdc.reader.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(viewLocation = "/views/main/Main.fxml",title = "幻想藏书阁")
public class MainView extends AbstractView {

    @Inject
    private FontawsomeService icon;

    @Inject
    private Logger logger;

    @Inject
    private ReaderView readerView;

    @Inject
    private ConfigureView configureView;

    private ToggleGroup group = new ToggleGroup();

    @PostConstruct
    public void init(){

        this.bindViews();

        ToggleButton mainView = findById("main");
        this.setIcon(mainView,"bookmark");
        mainView.setSelected(true);
        mainView.setUserData(ReaderView.class);

        group.getToggles().add(mainView);

        ToggleButton configs = findById("conf");
        configs.setUserData(ConfigureView.class);
        this.setIcon(configs,"gear");

        group.getToggles().add(configs);

        group.selectedToggleProperty().addListener(this::onToggleChange);

        this.changeView();
    }

    private void bindViews(){
        Stage stage = this.getStage();
        stage.setMinHeight(580);
        stage.setMinWidth(1000);

        BorderPane reader = (BorderPane)readerView.getView();
        reader.prefHeightProperty().bind(stage.heightProperty().subtract(12));
        reader.prefWidthProperty().bind(stage.widthProperty().subtract(84));

        BorderPane config = (BorderPane)configureView.getView();
        config.prefHeightProperty().bind(stage.heightProperty().subtract(12));
        config.prefWidthProperty().bind(stage.widthProperty().subtract(84));


        // 不知道为什么，view被首次set到center的时候，在窗口中不会显示此view。
        // 在这里首先把每一个view分别set到center一遍，防止这种情况。
        // 我可以确信不是本框架的问题，在出现此问题的时候view组件均已经初始化完毕。
        BorderPane root = this.findById("content");
        root.setCenter(config);
        // 主界面。
        root.setCenter(reader);
    }

    private void changeView() {
        ToggleButton view = (ToggleButton) group.getSelectedToggle();
        BorderPane root = this.findById("content");
        Class userData = (Class) view.getUserData();
        if (ReaderView.class.equals(userData)) {
            root.setCenter(readerView.getView());
        } else if (ConfigureView.class.equals(userData)) {
            root.setCenter(configureView.getView());
        }
        root.requestLayout();
    }

    private void onToggleChange(Observable toggleObs, Toggle old, Toggle toggle){
        if (toggle == null) {
            old.setSelected(true);
        }
        this.changeView();
    }

    private void setIcon(ButtonBase btn, String name) {
        btn.setFont(icon.getFont(FontSize.MIDDLE_SMALL));
        btn.setText(icon.getFontIcon(name));
        btn.setPadding(new Insets(4,6,4,6));
    }

}

package org.swdc.reader.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.swdc.dependency.event.Events;
import org.swdc.fx.FXResources;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.events.TypeSelectEvent;
import org.swdc.reader.ui.cells.TypeCell;
import org.swdc.reader.ui.dialogs.mainview.EditTypeDialog;

import java.util.List;
import java.util.ResourceBundle;

@View(viewLocation = "/views/main/ReaderView.fxml")
public class ReaderView extends AbstractView {

    @Inject
    private FontawsomeService icon;

    @Inject
    private EditTypeDialog editTypeDialog;

    @Inject
    private Logger logger;

    @Inject
    private FXResources resources;

    @PostConstruct
    public void init(){
        Button open = this.findById("open");
        this.setIcon(open,"folder");

        Button refFolder = this.findById("refresh");
        this.setIcon(refFolder,"refresh");

        Button add = this.findById("plus");
        this.setIcon(add,"plus");

        Button imp = this.findById("import");
        this.setIcon(imp,"download");

        Button help = this.findById("help");
        this.setIcon(help,"question");

        Button search = this.findById("find");
        this.setIcon(search,"search");

        ListView<BookType> typeListView = this.findById("typeList");
        typeListView.setCellFactory((ListView<BookType> lv) -> new TypeCell(icon,editTypeDialog));

        ContextMenu menuTypes = new ContextMenu();

        ResourceBundle resourceBundle = resources.getResourceBundle();

        MenuItem create = new MenuItem(resourceBundle.getString(LanguageKeys.KEY_MENU_ADD_TYPE));
        create.setOnAction((e) -> ReaderViewController.class.cast(this.getController()).showAddType());

        MenuItem remove = new MenuItem(resourceBundle.getString(LanguageKeys.KEY_MENU_DELETE_TYPE));
        remove.setOnAction((e) -> ReaderViewController.class.cast(this.getController()).showRemoveType());
        MenuItem edit = new MenuItem(resourceBundle.getString(LanguageKeys.KEY_MENU_EDIT_TYPE));
        edit.setOnAction((e) -> ReaderViewController.class.cast(this.getController()).showEditType());

        MenuItem openFile = new MenuItem(resourceBundle.getString(LanguageKeys.KEY_MENU_IMPORT));

        menuTypes.getItems().addAll(create,remove,edit,openFile);
        typeListView.setContextMenu(menuTypes);

        typeListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.emit(new TypeSelectEvent(newValue));
            } else {
                this.emit(new TypeSelectEvent(null));
            }
        }));


        this.emit(new TypeListRefreshEvent(""));

        this.getStage().setOnCloseRequest((e) -> {
            ReaderViewController controller = this.getController();
            controller.beforeHide(null);
        });
    }

    private void setIcon(ButtonBase btn, String name) {
        btn.setFont(icon.getFont(FontSize.SMALL));
        btn.setText(icon.getFontIcon(name));
        btn.setPadding(new Insets(4,6,4,6));
    }

    @Override
    protected void closed() {
        ReaderViewController controller = this.getController();
        controller.beforeHide(null);
        super.closed();
    }
}

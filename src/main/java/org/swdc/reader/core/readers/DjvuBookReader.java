package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.DjvuLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.LanguageKeys;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class DjvuBookReader implements BookReader<Image> {

    private Canvas canvas;
    private ScrollPane pane;

    private DjvuLocator locator;

    // 章节名
    private TextField chapterName = new TextField();
    // 页码和跳转
    private TextField jump = new TextField();
    private BorderPane panel = new BorderPane();
    private Image data;
    private Book book;
    private TOCAndFavoriteDialog dialog;

    private Executor executor;

    public static class Builder{

        private List<RenderResolver> resolvers;
        private PDFConfig config;
        private Book book;
        private File assets;
        private TOCAndFavoriteDialog dialog;
        private ThreadPoolExecutor executor;

        private ResourceBundle bundle;

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder tocDialog(TOCAndFavoriteDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public Builder resolvers(List<RenderResolver> resolvers) {
            this.resolvers = resolvers;
            return this;
        }

        public Builder executor(ThreadPoolExecutor executor) {
            this.executor = executor;
            return this;
        }

        public Builder bundle(ResourceBundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder assetsFolder(File file) {
            this.assets = file;
            return this;
        }

        public DjvuBookReader build() {
            DjvuBookReader reader = new DjvuBookReader(bundle);
            reader.setBook(book);
            reader.setDialog(this.dialog);
            reader.setExecutor(this.executor);
            DjvuLocator locator = new DjvuLocator(book,config,assets);
            executor.submit(() -> {
                locator.indexOutlines((location, title) -> {
                    String realTitle = title == null ? bundle.getString(LanguageKeys.KEY_TXT_CHAPTER)
                            .replace("#pageNo",location) : title;
                    ContentsItem item = new ContentsItem();
                    item.setLocated(this.book);
                    item.setTitle(realTitle);
                    item.setLocation(location);
                    dialog.buildTableOfContent(item);
                });
            });
            reader.setLocator(locator);
            reader.goNextPage();
            return reader;
        }

    }


    public DjvuBookReader(ResourceBundle bundle) {
        HBox left = new HBox();
        left.setAlignment(Pos.CENTER_LEFT);
        left.setSpacing(8);

        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setSpacing(8);

        HBox center = new HBox();
        HBox.setHgrow(center, Priority.ALWAYS);

        HBox tools = new HBox();
        tools.setAlignment(Pos.CENTER_LEFT);
        tools.setPadding(new Insets(8,12,8,12));
        tools.setSpacing(16);
        tools.getStyleClass().add("reader-tools");

        Button prev = new Button(bundle.getString(LanguageKeys.KEY_TXT_PREV_PAGE));
        prev.setOnAction((e) -> this.goPreviousPage());

        Button showTools = new Button(bundle.getString(LanguageKeys.KEY_TXT_OPT));

        Button bookMark = new Button(bundle.getString(LanguageKeys.KEY_TXT_MARK));
        bookMark.setOnAction(e -> dialog.showMarks(this));
        Button contents = new Button(bundle.getString(LanguageKeys.KEY_TXT_TOC));
        contents.setOnAction((e) -> dialog.showTableOfContent(this));

        Button jumpTo = new Button(bundle.getString(LanguageKeys.KEY_TXT_JUMP));
        jumpTo.setOnAction((e) -> {
            String target = jump.getText();
            try {
                Integer num = Integer.valueOf(target);
                this.goTo(num);
            }catch (Exception ex) {
                jump.setText(locator.getLocation());
            }
        });
        Button next = new Button(bundle.getString(LanguageKeys.KEY_TXT_NEXT_PAGE));
        next.setOnAction((e) -> this.goNextPage());

        chapterName.setPrefWidth(240);
        left.getChildren().addAll(prev,chapterName,jump,jumpTo,next);
        right.getChildren().addAll(contents,showTools,bookMark);
        tools.getChildren().addAll(left,center,right);
        this.panel.setTop(tools);
        this.panel.setCenter(this.pane);
        this.panel.getStyleClass().add("reader-content");
        this.panel.setOnKeyPressed(KeyEvent::consume);
    }

    private void create() {
        canvas = new Canvas();
        pane = new ScrollPane(canvas);
        pane.widthProperty().removeListener(this::onResize);
        pane.heightProperty().removeListener(this::onResize);
        pane.widthProperty().addListener(this::onResize);
        pane.heightProperty().addListener(this::onResize);
        canvas.widthProperty().bind(pane.widthProperty());

        panel.setCenter(pane);
    }

    private void onResize(ObservableValue value, Object oldVal, Object newVal) {
        if (canvas == null || locator == null || pane == null || panel == null || data == null) {
            return;
        }
        Image pageData = data;
        // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
        double height = (canvas.getWidth() * pageData.getHeight())/ pageData.getWidth();
        canvas.setHeight(height);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.LIGHTGRAY);
        context.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        context.drawImage(pageData,0,0,canvas.getWidth(), height);
    }

    @Override
    public Node getView() {
        if (this.pane == null) {
            this.create();
        }
        return this.panel;
    }

    @Override
    public void renderPage() {
        Platform.runLater(() -> {
            if (this.pane == null) {
                this.create();
            }
            pane.setHvalue((panel.getWidth() / 2) - (data.getWidth() / 2));
            pane.setVvalue(0);
            // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
            double height = (canvas.getWidth() * data.getHeight())/ data.getWidth();
            canvas.setHeight(height);
            GraphicsContext context = canvas.getGraphicsContext2D();
            context.setFill(Color.LIGHTGRAY);
            context.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
            context.drawImage(data,0,0,canvas.getWidth(), height);

            this.jump.setText(locator.getLocation());
            this.chapterName.setText(locator.getTitle());
        });
    }

    @Override
    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.data = this.locator.nextPage();
            this.renderPage();
        });
    }

    @Override
    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.data = this.locator.prevPage();
            this.renderPage();
        });
    }

    @Override
    public <T> void goTo(T location) {
        if (this.locator == null) {
            return;
        }
        this.data = this.locator.toPage(location.toString());
        this.renderPage();
    }

    @Override
    public void close() {
        this.locator.finalizeResources();
        pane.widthProperty().removeListener(this::onResize);
        pane.heightProperty().removeListener(this::onResize);
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setDialog(TOCAndFavoriteDialog dialog) {
        this.dialog = dialog;
    }

    public TOCAndFavoriteDialog getDialog() {
        return dialog;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setLocator(DjvuLocator locator) {
        this.locator = locator;
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public String getLocation() {
        return locator.getLocation();
    }

    @Override
    public String getChapterName() {
        return locator.getTitle();
    }
}

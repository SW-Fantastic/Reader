package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.PDFLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class PDFBookReader implements BookReader<Image> {

    private Canvas canvas;
    private ScrollPane pane;

    private PDFLocator locator;

    // 章节名
    private TextField chapterName = new TextField();
    // 页码和跳转
    private TextField jump = new TextField();
    private BorderPane panel = new BorderPane();
    private Image data;
    private Book book;
    private TOCAndFavoriteDialog dialog;

    public static class Builder{

        private List<RenderResolver> resolvers;
        private PDFConfig config;
        private Book book;
        private CodepageDetectorProxy proxy;
        private File assets;
        private TOCAndFavoriteDialog dialog;
        private ThreadPoolExecutor executor;

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder config(PDFConfig config) {
            this.config = config;
            return this;
        }

        public Builder tocDialog(TOCAndFavoriteDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public Builder codeDet(CodepageDetectorProxy proxy) {
            this.proxy = proxy;
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

        public Builder assetsFolder(File file) {
            this.assets = file;
            return this;
        }

        public PDFBookReader build() {
            PDFBookReader reader = new PDFBookReader();
            reader.setBook(book);
            reader.setDialog(this.dialog);
            PDFLocator locator = new PDFLocator(book,config,assets);
            executor.submit(() -> {
                locator.indexOutlines((location, title) -> {
                    String realTitle = title == null ? "第" + location + "页" : title;
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

    public PDFBookReader() {
        HBox tools = new HBox();
        tools.setAlignment(Pos.CENTER);
        tools.setPadding(new Insets(8,4,8,4));
        tools.setSpacing(16);
        tools.getStyleClass().add("reader-tools");

        Button prev = new Button("上一页");
        prev.setOnAction((e) -> this.goPreviousPage());

        Button showTools = new Button("选项");

        Button bookMark = new Button("书签");
        bookMark.setOnAction(e -> dialog.showMarks(this));
        Button contents = new Button("目录");
        contents.setOnAction((e) -> dialog.showTableOfContent(this));

        Button jumpTo = new Button("跳转");
        jumpTo.setOnAction((e) -> {
            String target = jump.getText();
            try {
                Integer num = Integer.valueOf(target);
                this.goTo(num);
            }catch (Exception ex) {
                jump.setText(locator.getLocation());
            }
        });
        Button next = new Button("下一页");
        next.setOnAction((e) -> this.goNextPage());

        chapterName.setPrefWidth(240);
        tools.getChildren().addAll(prev,contents,showTools,chapterName,jump,jumpTo,bookMark,next);
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

    protected void setBook(Book book) {
        this.book = book;
    }

    public void setLocator(PDFLocator locator) {
        this.locator = locator;
    }

    private void setDialog(TOCAndFavoriteDialog dialog) {
        this.dialog = dialog;
    }


    @Override
    public BorderPane getView() {
        if (this.pane == null) {
            this.create();
        }
        return this.panel;
    }

    @Override
    public void renderPage() {
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
    }

    @Override
    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        this.data = this.locator.nextPage();
        this.renderPage();
    }

    @Override
    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        this.data = this.locator.prevPage();
        this.renderPage();
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

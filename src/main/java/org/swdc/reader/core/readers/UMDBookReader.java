package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.core.locators.UMDLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class UMDBookReader implements BookReader<String> {

    private TOCAndFavoriteDialog tocDialog;

    private Book book;

    private ExecutorService executor;

    private UMDLocator locator;

    private String text;

    private WebView view = new WebView();
    // 章节名
    private TextField chapterName = new TextField();
    // 页码和跳转
    private TextField jump = new TextField();
    private BorderPane panel = new BorderPane();

    public static class Builder{

        private List<RenderResolver> resolvers;
        private TextConfig config;
        private Book book;
        private CodepageDetectorProxy proxy;
        private File assets;
        private TOCAndFavoriteDialog dialog;
        private ThreadPoolExecutor exec;

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder config(TextConfig config) {
            this.config = config;
            return this;
        }

        public Builder exec(ThreadPoolExecutor executor) {
            this.exec = executor;
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

        public Builder assetsFolder(File file) {
            this.assets = file;
            return this;
        }

        public Builder tocDialog(TOCAndFavoriteDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public UMDBookReader build() {

            UMDBookReader reader = new UMDBookReader();
            reader.setTocDialog(this.dialog);
            reader.setBook(this.book);
            reader.setExecutor(exec);

            exec.submit(() -> {
                UMDLocator locator = new UMDLocator(resolvers,book,this.proxy,this.config,this.assets);
                reader.setLocator(locator);
                Platform.runLater(() -> {
                    reader.goNextPage();
                });
                locator.indexChapters((title, location) -> {
                    ContentsItem item = new ContentsItem();
                    item.setTitle(title);
                    item.setLocated(book);
                    item.setLocation(location);
                    dialog.buildTableOfContent(item);
                });
            });

            return reader;
        }

    }

    protected UMDBookReader() {

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

        Button prev = new Button("上一页");
        prev.setOnAction((e) -> this.goPreviousPage());

        Button showTools = new Button("选项");

        Button bookMark = new Button("书签");
        bookMark.setOnAction(e -> tocDialog.showMarks(this));
        Button contents = new Button("目录");
        contents.setOnAction((e) -> tocDialog.showTableOfContent(this));

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
        left.getChildren().addAll(prev,chapterName,jump,jumpTo,next);
        right.getChildren().addAll(contents,showTools,bookMark);
        tools.getChildren().addAll(left,center,right);
        this.panel.setTop(tools);
        this.panel.setCenter(this.view);
        this.panel.getStyleClass().add("reader-content");
        this.panel.setOnKeyPressed(KeyEvent::consume);
    }

    public void setLocator(UMDLocator locator) {
        this.locator = locator;
    }

    public UMDLocator getLocator() {
        return locator;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setTocDialog(TOCAndFavoriteDialog tocDialog) {
        this.tocDialog = tocDialog;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public Node getView() {
        return panel;
    }

    @Override
    public void renderPage() {
        if (this.locator == null) {
            return;
        }
        Platform.runLater(() ->{
            chapterName.setText(locator.getTitle());
            jump.setText(locator.getLocation());
            view.getEngine().loadContent(text);
        });
    }

    @Override
    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.text = this.locator.nextPage();
            this.renderPage();
        });
    }

    @Override
    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.text = this.locator.prevPage();
            this.renderPage();
        });
    }

    @Override
    public <T> void goTo(T location) {
        if (this.locator == null || location == null) {
            return;
        }
        this.text = locator.toPage(location.toString());
        this.renderPage();
    }

    @Override
    public void close() {
        locator.finalizeResources();
        this.locator = null;
        this.book = null;
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

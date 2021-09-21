package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.ChmBookLocator;
import org.swdc.reader.core.locators.EpubLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ChmBookReader implements BookReader<String> {

    private ChmBookLocator locator;
    private TextConfig textConfig;
    private List<RenderResolver> resolvers;
    private WebView view = new WebView();
    // 章节名
    private TextField chapterName = new TextField();
    // 页码和跳转
    TextField jump = new TextField();
    private BorderPane panel = new BorderPane();
    private String data;
    private Book book;
    private TOCAndFavoriteDialog dialog;

    private ThreadPoolExecutor poolExecutor;



    public static class Builder {

        private List<RenderResolver> resolvers;
        private TextConfig config;
        private Book book;
        private CodepageDetectorProxy proxy;
        private File assets;
        private TOCAndFavoriteDialog dialog;
        private ThreadPoolExecutor executor;


        public ChmBookReader.Builder book(Book book) {
            this.book = book;
            return this;
        }

        public ChmBookReader.Builder config(TextConfig config, EpubConfig epubConfig) {
            this.config = config;
            //this.epubConfig = epubConfig;
            return this;
        }

        public ChmBookReader.Builder tocDialog(TOCAndFavoriteDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public ChmBookReader.Builder codeDet(CodepageDetectorProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public ChmBookReader.Builder resolvers(List<RenderResolver> resolvers) {
            this.resolvers = resolvers;
            return this;
        }

        public ChmBookReader.Builder executor(ThreadPoolExecutor executor) {
            this.executor = executor;
            return this;
        }

        public ChmBookReader.Builder assetsFolder(File file) {
            this.assets = file;
            return this;
        }

        public ChmBookReader build() {
            ChmBookReader reader = new ChmBookReader();
            reader.setBook(book);
            reader.setDialog(this.dialog);
            reader.setPoolExecutor(executor);
            reader.setLocator(new ChmBookLocator(this.book,this.assets));

            //executor.submit(() -> {
                /*EpubLocator locator = new EpubLocator(resolvers,book,epubConfig,config,assets);
                reader.setLocator(locator);

                Platform.runLater(() -> {
                    reader.goNextPage();
                });

                locator.indexContents((toc, idx) -> {
                    String title = toc.getTitle();
                    if (title == null || title.isEmpty()) {
                        title = "第 " + idx  + "章";
                    }
                    ContentsItem item = new ContentsItem();
                    item.setTitle(title);
                    item.setLocation(toc.getResource().getHref());
                    item.setLocated(book);
                    dialog.buildTableOfContent(item);
                });*/
           // });

            reader.goNextPage();
            return reader;
        }

    }

    public ChmBookReader(){

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
        this.panel.setCenter(this.view);
        this.panel.getStyleClass().add("reader-content");
        this.panel.setOnKeyPressed(KeyEvent::consume);

        this.view.getEngine().locationProperty().addListener((obs,oldVal, newVal) -> {
            try {
                String path = new URL(newVal).getPath();
                locator.location(path);
                System.err.println(path);
            } catch (Exception e) {

            }
        });
    }

    private void setPoolExecutor(ThreadPoolExecutor poolExecutor) {
        this.poolExecutor = poolExecutor;
    }

    public Book getBook() {
        return book;
    }

    private void setDialog(TOCAndFavoriteDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public String getLocation() {
        if (this.locator == null) {
            return null;
        }
        return this.locator.getLocation();
    }

    @Override
    public String getChapterName() {
        return this.locator.getTitle();
    }

    protected void setBook(Book book) {
        this.book = book;
    }

    public void setLocator(ChmBookLocator locator) {
        this.locator = locator;
    }

    @Override
    public BorderPane getView() {
        return panel;
    }

    @Override
    public void renderPage() {
        if (this.locator == null) {
            return;
        }
        chapterName.setText(locator.getTitle());
        jump.setText(locator.getLocation());
        view.getEngine().load(locator.location());
    }

    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        locator.nextLocation();
        renderPage();
    }

    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        locator.prevLocation();
        renderPage();
    }

    @Override
    public <String> void goTo(String page) {
        if (this.locator == null || page == null) {
            return;
        }
        this.data = locator.toPage(page.toString());
        this.renderPage();
    }

    @Override
    public void close() {
        this.locator.finalizeResources();
        this.locator = null;
        this.book = null;
    }
}

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
import org.swdc.reader.core.locators.MobiLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.LanguageKeys;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class MobiBookReader implements BookReader<String> {


    private TextConfig textConfig;
    private MobiLocator locator;
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

    private Executor executor;



    public static class Builder{

        private List<RenderResolver> resolvers;
        private TextConfig config;
        private Book book;
        private CodepageDetectorProxy proxy;
        private File assets;
        private ThreadPoolExecutor executor;
        private TOCAndFavoriteDialog dialog;

        private ResourceBundle bundle;

        public Builder bundle(ResourceBundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder dialog(TOCAndFavoriteDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public Builder config(TextConfig config) {
            this.config = config;
            return this;
        }

        public Builder executor(ThreadPoolExecutor executor) {
            this.executor = executor;
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



        public MobiBookReader build() {

            MobiBookReader reader = new MobiBookReader(this.bundle);
            MobiLocator locator = new MobiLocator(resolvers,executor,book,config,this.assets);
            reader.setLocator(locator);
            reader.setBook(book);
            reader.setDialog(dialog);
            reader.setExecutor(executor);

            executor.submit(() -> {
                locator.indexTableOfContents((idx) -> {
                    ContentsItem contentsItem = new ContentsItem();
                    contentsItem.setLocation(idx.toString());
                    contentsItem.setTitle(
                            bundle.getString(LanguageKeys.KEY_TXT_CHAPTER)
                                    .replace("#pageNo",idx + "")
                    );
                    contentsItem.setLocated(this.book);
                    dialog.buildTableOfContent(contentsItem);
                });
            });

            reader.goNextPage();
            return reader;
        }

    }

    public MobiBookReader(ResourceBundle bundle) {


        HBox  left = new HBox();
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
        this.panel.setCenter(this.view);
        this.panel.getStyleClass().add("reader-content");
        this.panel.setOnKeyPressed(KeyEvent::consume);
    }

    public void setLocator(MobiLocator locator) {
        this.locator = locator;
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

    public Book getBook() {
        return book;
    }

    protected void setBook(Book book) {
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
            view.getEngine().loadContent(data);
        });
    }

    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.data = locator.nextPage();
            this.renderPage();
        });
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        executor.execute(() -> {
            this.data = locator.prevPage();
            this.renderPage();
        });
    }

    @Override
    public <Integer> void goTo(Integer page) {
        if (this.locator == null || page == null) {
            return;
        }
        executor.execute(() -> {
            this.data = locator.toPage(page.toString());
            this.renderPage();
        });
    }

    @Override
    public void close() {
        this.locator.finalizeResources();
        this.locator = null;
        this.book = null;
    }

}

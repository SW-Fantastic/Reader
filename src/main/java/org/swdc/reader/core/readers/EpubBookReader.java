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
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.EpubLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.LanguageKeys;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

@MultipleImplement(BookReader.class)
public class EpubBookReader implements BookReader<String> {

    private EpubLocator locator;
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


    public static class Builder{

        private List<RenderResolver> resolvers;
        private TextConfig config;
        private EpubConfig epubConfig;
        private Book book;
        private CodepageDetectorProxy proxy;
        private File assets;
        private TOCAndFavoriteDialog dialog;
        private ThreadPoolExecutor executor;

        private ResourceBundle bundle;

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder bundle(ResourceBundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder config(TextConfig config,EpubConfig epubConfig) {
            this.config = config;
            this.epubConfig = epubConfig;
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

        public EpubBookReader build() {
            EpubBookReader reader = new EpubBookReader(bundle);
            reader.setBook(book);
            reader.setDialog(this.dialog);
            reader.setPoolExecutor(executor);

            executor.submit(() -> {
                EpubLocator locator = new EpubLocator(resolvers,book,epubConfig,config,assets);
                reader.setLocator(locator);

                Platform.runLater(() -> {
                    reader.goNextPage();
                });

                locator.indexContents((toc, idx) -> {
                    String title = toc.getTitle();
                    if (title == null || title.isEmpty()) {
                        title = bundle.getString(LanguageKeys.KEY_TXT_CHAPTER).replace("#pageNo",idx + "");
                    }
                    ContentsItem item = new ContentsItem();
                    item.setTitle(title);
                    item.setLocation(toc.getResource().getHref());
                    item.setLocated(book);
                    dialog.buildTableOfContent(item);
                });
            });


           return reader;
        }

    }

    public EpubBookReader(ResourceBundle bundle){

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

    public void setLocator(EpubLocator locator) {
        this.locator = locator;
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
        Platform.runLater(() -> {
            chapterName.setText(locator.getTitle());
            jump.setText(locator.getLocation());
            view.getEngine().loadContent(data);
        });
    }

    public void goNextPage() {
        if (this.locator == null) {
            return;
        }
        poolExecutor.execute(() -> {
            this.data = locator.nextPage();
            this.renderPage();
        });
    }

    public void goPreviousPage() {
        if (this.locator == null) {
            return;
        }
        poolExecutor.execute(() -> {
            this.data = locator.prevPage();
            this.renderPage();
        });
    }

    @Override
    public <Integer> void goTo(Integer page) {
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

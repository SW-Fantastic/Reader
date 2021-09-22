package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jchmlib.ChmTopicsTree;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.core.locators.ChmBookLocator;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ChmBookReader implements BookReader<String> {

    private ChmBookLocator locator;
    private TextConfig textConfig;
    private List<RenderResolver> resolvers;
    private WebView view = new WebView();
    private BorderPane panel = new BorderPane();
    private String data;
    private Book book;
    private TOCAndFavoriteDialog dialog;

    private TreeItem<ChmTopicsTree> topicRoot;
    private TreeView<ChmTopicsTree> topic;

    private ThreadPoolExecutor poolExecutor;
    private SplitPane splitPane;

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


            reader.goNextPage();
            return reader;
        }

    }

    public ChmBookReader(){

        HBox tools = new HBox();
        tools.setAlignment(Pos.CENTER_LEFT);
        tools.setPadding(new Insets(8,4,8,4));
        tools.setSpacing(16);
        tools.getStyleClass().add("reader-tools");

        Button prev = new Button("上一页");
        prev.setOnAction((e) -> this.goPreviousPage());
        Button next = new Button("下一页");
        next.setOnAction((e) -> this.goNextPage());

        tools.getChildren().addAll(prev,next);
        this.panel.setTop(tools);
        this.panel.setCenter(this.view);
        this.panel.getStyleClass().add("reader-content");
        this.panel.setOnKeyPressed(KeyEvent::consume);

        this.topic = new TreeView<>();
        this.topicRoot = new TreeItem<>();
        this.topic.setRoot(topicRoot);
        this.topic.setShowRoot(false);
        this.topic.setMaxWidth(240);
        this.topicRoot.setExpanded(true);

        this.topic.setOnMouseClicked(e -> {
            TreeItem<ChmTopicsTree> item = this.topic.getSelectionModel().getSelectedItem();
            if (item == null || item.getValue() == null) {
                return;
            }
            String path = "vchm://" + Base64.getEncoder()
                    .encodeToString(locator.getFile().getAbsolutePath().getBytes(StandardCharsets.UTF_8))
                    + "|" + item.getValue().path;
            view.getEngine().load(path);
        });

        this.splitPane = new SplitPane();
        this.splitPane.getItems().add(this.topic);
        this.splitPane.getItems().add(this.panel);

        this.view.getEngine().locationProperty().addListener((obs,oldVal, newVal) -> {
            try {
                String path = new URL(newVal).getPath();
                locator.location(path);
                System.err.println(path);
            } catch (Exception e) {

            }
        });
    }

    private void parseTopic(TreeItem<ChmTopicsTree> item,ChmTopicsTree val) {
        if (val == null) {
            val = locator.getChmFile().getTopicsTree();
        }
        if (item == null) {
            item = topicRoot;
        }
        if (val == null && item == topicRoot) {
            this.splitPane.getItems().remove(topic);
            return;
        }
        List<ChmTopicsTree> children = val.children;
        ObservableList<TreeItem<ChmTopicsTree>> items = item.getChildren();
        items.clear();
        for (ChmTopicsTree tree: children) {
            TreeItem<ChmTopicsTree> node = new TreeItem<>(tree,new Label(tree.title));
            items.add(node);
            if (tree.children != null && tree.children.size() > 0) {
                parseTopic(node,tree);
            }
        }
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
        parseTopic(null,null);
    }

    @Override
    public Node getView() {
        return splitPane;
    }

    @Override
    public void renderPage() {
        if (this.locator == null) {
            return;
        }
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

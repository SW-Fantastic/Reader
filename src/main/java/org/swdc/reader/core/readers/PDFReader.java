package org.swdc.reader.core.readers;

import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.locators.PDFLocator;
import org.swdc.reader.core.views.PDFRenderView;
import org.swdc.reader.entity.Book;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/6/9.
 */
@Component
public class PDFReader implements BookReader<Image> {

    private PDFLocator locator;

    private Book currentBook;

    @Autowired
    private PDFRenderView view;

    @Autowired
    private PDFConfig config;

    @PostConstruct
    protected void init() {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    @Override
    public void setBook(Book book) {
        if (locator != null) {
            locator.finalizeResources();
            locator = null;
        }
        locator = new PDFLocator(book, config);
        this.currentBook = book;
    }

    @Override
    public Book getBook() {
        return currentBook;
    }

    @Override
    public void renderPage(Image pageData, BorderPane view) {
        if (pageData == null) {
            return;
        }
        ScrollPane pane = (ScrollPane) view.lookup("#pdfRenderView");
        if (pane == null) {
            view.setCenter(this.view.getView());
            pane = (ScrollPane)this.view.getView();
            pane.widthProperty().removeListener(this::onResize);
            pane.heightProperty().removeListener(this::onResize);
            pane.widthProperty().addListener(this::onResize);
            pane.heightProperty().addListener(this::onResize);
        }
        pane.setHvalue((view.getWidth() / 2) - (pageData.getWidth() / 2));
        pane.setVvalue(0);

        Canvas canvasView = (Canvas) ((ScrollPane)this.view.getView()).getContent();
        // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
        double height = (canvasView.getWidth() * pageData.getHeight())/ pageData.getWidth();
        canvasView.setHeight(height);
        GraphicsContext context = canvasView.getGraphicsContext2D();
        context.setFill(Color.LIGHTGRAY);
        context.clearRect(0,0,canvasView.getWidth(), canvasView.getHeight());
        context.drawImage(pageData,0,0,canvasView.getWidth(), height);
    }

    private void onResize(ObservableValue value, Object oldVal, Object newVal) {
        Image pageData = locator.currentPage();
        Canvas canvasView = (Canvas) ((ScrollPane)this.view.getView()).getContent();
        // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
        double height = (canvasView.getWidth() * pageData.getHeight())/ pageData.getWidth();
        canvasView.setHeight(height);
        GraphicsContext context = canvasView.getGraphicsContext2D();
        context.setFill(Color.LIGHTGRAY);
        context.clearRect(0,0,canvasView.getWidth(), canvasView.getHeight());
        context.drawImage(pageData,0,0,canvasView.getWidth(), height);
    }

    @Override
    public boolean isSupport(Book target) {
        return target.getMimeData().equals("application/pdf") &&
                    target.getName().toLowerCase().endsWith("pdf");
    }

    @Override
    public BookLocator<Image> getLocator() {
        return locator;
    }

}

package org.swdc.reader.core.readers;

import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.ext.ExternalRenderAction;
import org.swdc.reader.core.locators.PDFLocator;
import org.swdc.reader.core.views.PDFRenderView;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/6/9.
 */
public class PDFReader extends AbstractReader<Image> {

    private PDFLocator locator;

    @Aware
    private PDFRenderView view = null;

    @Aware
    private PDFConfig config = null;

    @Override
    public void initialize() {
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

        Image data = pageData;
        for (ExternalRenderAction<Image> action: renderActionObservableMap.values()) {
            data = action.process(data);
        }

        Canvas canvasView = (Canvas) ((ScrollPane)this.view.getView()).getContent();
        // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
        double height = (canvasView.getWidth() * data.getHeight())/ data.getWidth();
        canvasView.setHeight(height);
        GraphicsContext context = canvasView.getGraphicsContext2D();
        context.setFill(Color.LIGHTGRAY);
        context.clearRect(0,0,canvasView.getWidth(), canvasView.getHeight());
        context.drawImage(data,0,0,canvasView.getWidth(), height);
    }

    @Override
    protected void reload() {
        ScrollPane pane = (ScrollPane) getView().getView();
        Image data = locator.currentPage();
        for (ExternalRenderAction<Image> action: renderActionObservableMap.values()) {
            data = action.process(data);
        }
        Canvas canvasView = (Canvas) ((ScrollPane)this.view.getView()).getContent();
        // 计算画布当前宽度的时候，让图片等比例缩放为当前宽度时候的高度
        double height = (canvasView.getWidth() * data.getHeight())/ data.getWidth();
        canvasView.setHeight(height);
        GraphicsContext context = canvasView.getGraphicsContext2D();
        context.setFill(Color.LIGHTGRAY);
        context.clearRect(0,0,canvasView.getWidth(), canvasView.getHeight());
        context.drawImage(data,0,0,canvasView.getWidth(), height);
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

    @Override
    public void finalizeResources() {
        super.finalizeResources();
        locator.finalizeResources();
        ScrollPane pane = (ScrollPane) view.getView();
        pane.widthProperty().removeListener(this::onResize);
        pane.heightProperty().removeListener(this::onResize);
    }

    @Override
    public PDFRenderView getView() {
        return view;
    }
}

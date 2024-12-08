package org.swdc.reader.core.locators;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.pdfium.PDFBitmap;
import org.swdc.pdfium.PDFDocument;
import org.swdc.pdfium.PDFPage;
import org.swdc.pdfium.PDFPageRotate;
import org.swdc.platforms.Unsafe;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.LanguageKeys;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

/**
 * 处理pdf文档的解析
 */
public class PDFLocator implements BookLocator<Image> {

    private Book bookEntity;

    private String chapter = "";

    private PDDocument document;

    //private PdfiumDocument pdfiumDocument;
    private PDFDocument pdfiumDocument;


    private PDFRenderer renderer;

    private Integer location = -1;

    private PDFConfig config;

    private Map<Integer, PixelBuffer<ByteBuffer>> locationDataMap = new HashMap<>();

    private Boolean available;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResourceBundle bundle;

    public PDFLocator(ResourceBundle bundle,Book book, PDFConfig config,File assetsFolder) {
        this.config = config;
        this.bookEntity = book;
        this.bundle = bundle;
        File file = new File(assetsFolder.getAbsolutePath() + "/library/" + book.getName());
        try {
            this.pdfiumDocument = new PDFDocument(file);
            this.document = Loader.loadPDF(file);
            this.renderer = new PDFRenderer(document);
            this.available = true;
        } catch (IOException e) {
           logger.error("载入PDF失败。",e);
        }
    }

    public void indexOutlines(BiConsumer<String,String> resolver) {
        try {
            PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
            if (outline == null) {
                return;
            }
            PDOutlineItem item = outline.getFirstChild();
            if (item == null) {
                return;
            }
            loadContentTree(item,resolver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContentTree(PDOutlineItem item,BiConsumer<String,String> resolver) throws IOException {
        if(item.getDestination() instanceof PDPageDestination) {
            PDPageDestination  destination = (PDPageDestination) item.getDestination();
            String title = item.getTitle();
            Integer location = document.getPages().indexOf(destination.getPage());
            resolver.accept(location + "", title);
            if (item.hasChildren()) {
                this.loadContentTree(item.getFirstChild(),resolver);
            }
            if (item.getNextSibling() != null) {
                item = item.getNextSibling();
                this.loadContentTree(item,resolver);
            }
        }
    }

    @Override
    public Image prevPage() {
        if (locationDataMap.size() > config.getRenderMapSize()) {
            for (PixelBuffer<ByteBuffer> buffer: locationDataMap.values()) {
                Unsafe.free(buffer.getBuffer());
            }
            locationDataMap.clear();
        }
        this.location --;
        if (this.location < 0) {
            return nextPage();
        }
        return renderPage(this.location);
    }

    @Override
    public Image nextPage() {
        if (locationDataMap.size() > config.getRenderMapSize()) {
            for (PixelBuffer<ByteBuffer> buffer: locationDataMap.values()) {
                Unsafe.free(buffer.getBuffer());
            }
            locationDataMap.clear();
        }
        if (location + 1 >= document.getNumberOfPages()) {
            return  renderPage(this.location);
        }
        this.location ++;
        return renderPage(this.location);
    }

    @Override
    public Image toPage(String location) {
        try {
            Integer page = Integer.valueOf(location);
            this.location = page;
            return renderPage(page);
        } catch (Exception e) {
            logger.error("载入失败。",e);
        }
        return null;
    }

    @Override
    public String getTitle() {
        return chapter;
    }

    @Override
    public String getLocation() {
        return location + "";
    }

    @Override
    public Image currentPage() {
        return renderPage(location);
    }

    @Override
    public void finalizeResources() {
        try {
            document.close();
            pdfiumDocument.close();
            if (locationDataMap != null) {
                for (PixelBuffer<ByteBuffer> buffer : locationDataMap.values()) {
                    Unsafe.free(buffer.getBuffer());
                }
                locationDataMap.clear();
                locationDataMap = null;
                this.available = false;
            }
        } catch (IOException e) {
            logger.error("无法释放资源",e);
        }
    }

    @Override
    public Boolean isAvailable() {
        return this.available;
    }

    public static void fromArgbInt(int[] argb, ByteBuffer buffer) {
        buffer.position(0);
        for (int i = 0; i < argb.length; i++) {
            buffer.put(4 * i   ,(byte) ((argb[i]      ) & 0xff));
            buffer.put(4 * i + 1,(byte) ((argb[i] >>  8) & 0xff));
            buffer.put(4 * i + 2,(byte) ((argb[i] >> 16) & 0xff));
            buffer.put(4 * i + 3,(byte) ((argb[i] >> 24) & 0xff));
        }
    }

    private Image renderPage(Integer page) {
        try {
            if (locationDataMap.containsKey(page) && locationDataMap.get(page) != null) {
                chapter = bundle.getString(LanguageKeys.KEY_TXT_CHAPTER)
                        .replace("#pageNo",page + "");
                PixelBuffer<ByteBuffer> theData = locationDataMap.get(page);
                return new WritableImage(theData);
            }

            PDFPage pdfiumPage = pdfiumDocument.getPage(page);
            PDFBitmap bitmap = pdfiumPage.renderPage(config.getRenderQuality().intValue(), PDFPageRotate.NO_ROTATE);
            byte[] data = bitmap.getBuffer();
            ByteBuffer renderedBuffer = Unsafe.malloc(data.length);
            renderedBuffer.put(data);

            PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<>(
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    renderedBuffer,
                    PixelFormat.getByteBgraPreInstance()
            );

            WritableImage theImage = new WritableImage(pixelBuffer);
            renderedBuffer.position(0);

            pdfiumPage.close();
            bitmap.close();

            locationDataMap.put(page, pixelBuffer);
            chapter = bundle.getString(LanguageKeys.KEY_TXT_CHAPTER)
                    .replace("#pageNo",page + "");;

            return theImage;
        } catch (Exception ex) {
            logger.error("",ex);
        }
        return null;
    }
}

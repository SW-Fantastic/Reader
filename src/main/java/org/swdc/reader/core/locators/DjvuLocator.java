package org.swdc.reader.core.locators;

import com.lizardtech.djvu.DjVuImageFilter;
import com.lizardtech.djvu.DjVuInfo;
import com.lizardtech.djvu.DjVuPage;
import com.lizardtech.djvu.Document;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.entity.Book;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class DjvuLocator implements BookLocator<Image> {

    private Document djvuDocument;
    private Book bookEntity;
    private Integer location = -1;
    private PDFConfig config;
    private Map<Integer, Image> locationDataMap = new WeakHashMap<>();
    private Boolean available;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DjvuLocator(Book book, PDFConfig config, File assetsFolder) {
        this.bookEntity = book;
        this.config = config;
        File file = new File(assetsFolder.getAbsolutePath() + "/library/" + book.getName());
        try {
            djvuDocument = new Document(file.toURI().toURL());
            this.available = true;
        } catch (IOException e) {
            logger.error("载入Djvu失败。",e);
        }
    }

    @Override
    public Image prevPage() {
        if (location <= 0) {
            return currentPage();
        }
        location = location - 1;
        return currentPage();
    }


    public void indexOutlines(BiConsumer<String, String> consumer) {
        for (int idx = 0; idx < djvuDocument.size(); idx ++) {
            consumer.accept(idx + "", "第" + idx + "页");
        }
    }


    private static BufferedImage convertToBufferedImage(java.awt.Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    @Override
    public Image nextPage() {
        int size = djvuDocument.size();
        if (locationDataMap.containsKey(location + 1)) {
            location = location + 1;
            return locationDataMap.get(location);
        }
        if (location < size - 1) {
            location = location + 1;
        }
        return currentPage();
    }

    @Override
    public Image toPage(String loc) {
        int target = Integer.parseInt(loc);
        if (target < djvuDocument.size()  && target > 0) {
            location = target;
        }
        return currentPage();
    }

    @Override
    public String getTitle() {
        return "第" + location + "页";
    }

    @Override
    public String getLocation() {
        return location + "";
    }

    @Override
    public Image currentPage() {
        if (locationDataMap.containsKey(location)) {
            return locationDataMap.get(location);
        }
        try {
            DjVuPage page = djvuDocument.getPage(location,DjVuPage.MAX_PRIORITY,true);
            DjVuInfo info = page.getInfoWait();
            DjVuImageFilter filter = new DjVuImageFilter(
                    new Rectangle(0,0,info.width,info.height),
                    new Dimension(info.width,info.height),
                    page,
                    true
            );
            java.awt.Image image = filter.getImage();
            BufferedImage swingImage = convertToBufferedImage(image);
            Image writable = SwingFXUtils.toFXImage(swingImage,null);
            locationDataMap.put(location,writable);
            return writable;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void finalizeResources() {
        locationDataMap.clear();
        djvuDocument = null;
        available = false;
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }
}

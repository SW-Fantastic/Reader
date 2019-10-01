package org.swdc.reader.core.locators;

import hu.webhejj.pdb.PDBReader;
import hu.webhejj.pdb.PalmDataBase;
import hu.webhejj.pdb.PalmRecord;
import hu.webhejj.pdb.mobi.MobiAdapter;
import hu.webhejj.pdb.mobi.MobiHeaderRecord;
import lombok.extern.apachecommons.CommonsLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BookProcessEvent;
import org.swdc.reader.event.ContentItemFoundEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.CommonComponents;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by lenovo on 2019/9/29.
 */
@CommonsLog
public class MobiLocator extends MobiAdapter implements BookLocator<String> {

    private PalmDataBase palmDataBase;

    private ApplicationConfig config;

    private int location = 1;

    private boolean available = true;

    private TextConfig textConfig;

    private String backgroundImageData;

    private static final String HTML5 = "<!DOCTYPE HTML>";

    public MobiLocator(Executor executor,ApplicationConfig appConfig, Book book, TextConfig config){
        PDBReader reader = new PDBReader();
        executor.execute(() -> {
            try {
                palmDataBase = reader.read(new File("./data/library/" + book.getName()));
                this.initialize(palmDataBase);
                this.config = appConfig;
                this.textConfig = config;
                if (book.getContentsItems() == null || book.getContentsItems().size() == 0) {
                    this.initContentItems(book);
                }
                if (textConfig.getEnableBackgroundImage()) {
                    ByteArrayOutputStream bot = new ByteArrayOutputStream();
                    DataInputStream backgroundInput = new DataInputStream(new FileInputStream(new File("configs/readerResources/text/" + textConfig.getBackgroundImage())));
                    byte[] data = new byte[1024];
                    while (backgroundInput.read(data) != -1) {
                        bot.write(data);
                    }
                    bot.flush();
                    backgroundInput.close();
                    backgroundImageData = Base64.getEncoder().encodeToString(bot.toByteArray());
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        });
    }

    private void initContentItems(Book book) {
        int recordCount = getHeaderRecord().getRecordCount();
        int counter = 0;
        int chapterCounter = 1;
        double totals = recordCount;
        for (int index = 1; index < recordCount; ++index) {
            double progress = 1 - (index / totals);
            BookProcessEvent processEvent = new BookProcessEvent(progress, "正在索引页面");
            config.publishEvent(processEvent);
            if (counter < 2) {
                counter++;
            } else {
                ContentsItem item = new ContentsItem();
                item.setLocated(book);
                item.setLocation(index + "");
                item.setTitle("第" + chapterCounter++ + "页");
                config.publishEvent(new ContentItemFoundEvent(item));
            }
        }
        BookProcessEvent processEvent = new BookProcessEvent(1.0, "");
        config.publishEvent(processEvent);
    }

    @Override
    public String prevPage() {
        if (location  - 4 < 1) {
            return "这已经是第一页了。";
        }
        List<PalmRecord> records = this.palmDataBase.getPalmRecords();
        MobiHeaderRecord header = getHeaderRecord();
        StringBuilder sb = new StringBuilder();
        for (int idx = this.location - 4; idx < this.location - 2; idx++ ) {
            sb.append(readTextRecord((records.get(idx)).getData(), header.getCompresson(), header.getTextEncoding().toCharset(), header.getExtraDataFlags()));
        }
        this.location = this.location - 2;
        String data = sb.toString();
        String document = renderImage(data);
        return document;
    }

    @Override
    public String nextPage() {
        int pages = getHeaderRecord().getRecordCount() / 2;
        if (pages < location + 1) {
            return "这已经是最后一页了。";
        }
        List<PalmRecord> records = this.palmDataBase.getPalmRecords();
        MobiHeaderRecord header = getHeaderRecord();
        StringBuilder sb = new StringBuilder();
        for (int idx = this.location; idx < this.location + 2; idx++ ) {
            sb.append(readTextRecord((records.get(idx)).getData(), header.getCompresson(), header.getTextEncoding().toCharset(), header.getExtraDataFlags()));
        }
        this.location = this.location + 2;
        String data = sb.toString();
        String document = renderImage(data);
        return document;
    }

    @Override
    public String toPage(String location) {
        int pages = getHeaderRecord().getRecordCount() / 2;
        Integer pageLocation = Integer.valueOf(location);
        if (pages < pageLocation + 1) {
            return "无效的位置。";
        } else if (pageLocation <= 0) {
            return "无效的位置。";
        }
        this.location = pageLocation;
        return nextPage();
    }

    private String renderImage(String data) {
        Base64.Encoder encoder = Base64.getEncoder();
        Document document = Jsoup.parse(data);
        Elements elements = document.getElementsByTag("img");
        for (Element elem: elements) {
            int imageIndex = Integer.valueOf(elem.attr("recindex"));
            byte[] image = getImageOfPage(imageIndex);
            elem.attr("src", "data:image/png;base64," + encoder.encodeToString(image));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("body {")
                .append("font-family: \"")
                .append(CommonComponents.getFontMap().containsKey(textConfig.getFontPath()) ? CommonComponents.getFontMap().get(textConfig.getFontPath()).getFamily():"Microsoft YaHei").append("\";")
                .append("font-color:").append(textConfig.getFontColor()).append(";")
                .append("font-size:").append(textConfig.getFontSize()).append("px;")
                .append("background-color:").append(textConfig.getBackgroundColor()).append(";")
                .append("overflow-wrap: break-word;")
                .append("word-wrap: break-word;")
                .append("-webkit-font-smoothing: antialiased;")
                .append("padding: 18px;");
        if (textConfig.getEnableBackgroundImage()) {
            sb.append("background-image: url(data:image/png;base64,").append(backgroundImageData).append(");");
        }
        sb.append("}")
                .append("img {")
                .append("max-width: 100%;")
                .append("}")
                .append("a {")
                .append("text-decoration: none;")
                .append("}")
                .append("li {")
                .append("padding: 12px;")
                .append("}");
        if (textConfig.getEnableBackgroundImage()) {
            sb.append("body>div{")
                    .append("background-color: rgba(255,255,255,0.8);")
                    .append("padding: 36px;")
                    .append("}");
        } else {
            sb.append("body>div{")
                    .append("padding: 24px")
                    .append("}");
        }
        sb.append("p {")
                .append("line-height: 2;")
                .append("text-indent: ").append(textConfig.getFontSize() *2 + "px;")
                .append("letter-spacing: 1.2px;");
        if (textConfig.getEnableTextShadow()){
            sb.append("text-shadow: 0px 0px 5px ").append(textConfig.getShadowColor()).append(";");
        }
        sb.append("}");
        return HTML5 + "<head><style>" + sb.toString() + "</style></head><body><div>" + document.html() + "</div></body></html>";
    }

    @Override
    public String getTitle() {
        return getLocation();
    }

    @Override
    public String getLocation() {
        return "" + (this.location / 2);
    }

    @Override
    public String currentPage() {
        return "" + (this.location / 2);
    }

    @Override
    public void finalizeResources() {
        this.available = false;
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }
}

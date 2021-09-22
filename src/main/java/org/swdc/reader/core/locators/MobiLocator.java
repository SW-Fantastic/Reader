package org.swdc.reader.core.locators;

import hu.webhejj.pdb.PDBReader;
import hu.webhejj.pdb.PalmDataBase;
import hu.webhejj.pdb.PalmRecord;
import hu.webhejj.pdb.mobi.MobiAdapter;
import hu.webhejj.pdb.mobi.MobiHeaderRecord;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.services.HelperServices;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lenovo on 2019/9/29.
 */
public class MobiLocator implements BookLocator<String> {

    private static class TextMobiAdapter extends MobiAdapter {

        public String getTextRecord(byte[] data, MobiHeaderRecord.Compression compression, Charset charset, int extraDataFlags) {
            return this.readTextRecord(data, compression, charset, extraDataFlags);
        }

    }

    private PalmDataBase palmDataBase;

    private TextMobiAdapter adapter;

    private int location = 1;

    private boolean available = true;

    private TextConfig textConfig;

    private static final String HTML5 = "<!DOCTYPE HTML>";

    private List<? extends RenderResolver> resolvers;

    public MobiLocator(List<? extends RenderResolver> resolvers, ThreadPoolExecutor executor, Book book, TextConfig config, File assets){
        try {
            this.resolvers = resolvers;
            PDBReader reader = new PDBReader();
            adapter = new TextMobiAdapter();
            File bookFile = new File(assets.getAbsolutePath() + "/library/" + book.getName());
            palmDataBase = reader.read(bookFile);
            adapter.initialize(palmDataBase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void indexTableOfContents(Consumer<Integer> pageIndexer) {
        int recordCount = adapter.getHeaderRecord().getRecordCount();
        for (int index = 1; index < recordCount / 2; ++index) {
            pageIndexer.accept(index);
        }
    }


    @Override
    public String prevPage() {
        if (location  - 4 < 1) {
            return "这已经是第一页了。";
        }
        List<PalmRecord> records = this.palmDataBase.getPalmRecords();
        MobiHeaderRecord header = adapter.getHeaderRecord();
        StringBuilder sb = new StringBuilder();
        for (int idx = this.location - 4; idx < this.location - 2; idx++ ) {
            sb.append(adapter.getTextRecord((records.get(idx)).getData(), header.getCompresson(), header.getTextEncoding().toCharset(), header.getExtraDataFlags()));
        }
        this.location = this.location - 2;
        String data = sb.toString();
        String document = renderImage(data);
        return document;
    }

    @Override
    public String nextPage() {
        int pages = adapter.getHeaderRecord().getRecordCount() / 2;
        if (pages < location + 1) {
            return "这已经是最后一页了。";
        }
        List<PalmRecord> records = this.palmDataBase.getPalmRecords();
        MobiHeaderRecord header = adapter.getHeaderRecord();
        StringBuilder sb = new StringBuilder();
        for (int idx = this.location; idx < this.location + 2; idx++ ) {
            sb.append(adapter.getTextRecord((records.get(idx)).getData(), header.getCompresson(), header.getTextEncoding().toCharset(), header.getExtraDataFlags()));
        }
        this.location = this.location + 2;
        String data = sb.toString();
        String document = renderImage(data);
        return document;
    }

    @Override
    public String toPage(String location) {
        int pages = adapter.getHeaderRecord().getRecordCount() / 2;
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
            byte[] image = adapter.getImageOfPage(imageIndex);
            elem.attr("src", "data:image/png;base64," + encoder.encodeToString(image));
        }
        StringBuilder sb = new StringBuilder();
        for (RenderResolver resolver : resolvers) {
           if (resolver.support(this.getClass())){
               resolver.renderStyle(sb);
               resolver.renderContent(document.body());
           }
        }
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
        return toPage(this.getLocation());
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

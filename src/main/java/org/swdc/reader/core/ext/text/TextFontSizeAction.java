package org.swdc.reader.core.ext.text;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.swdc.reader.core.ext.ExternalRenderAction;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class TextFontSizeAction implements ExternalRenderAction<String> {

    private int fontSize = 0;

    public TextFontSizeAction(int size) {
        this.fontSize = size;
    }

    @Override
    public String process(String data) {
        Document document = Jsoup.parse(data);
        Element style = document.selectFirst("style");
        style.text(style.html().replaceAll("font-size:[0-9]+px","font-size:" + fontSize + "px"));
        return document.html().replace("&gt;",">");
    }

    public int getFontSize() {
        return fontSize;
    }
}

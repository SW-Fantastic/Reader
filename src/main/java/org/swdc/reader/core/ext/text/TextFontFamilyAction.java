package org.swdc.reader.core.ext.text;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.swdc.reader.core.ext.ExternalRenderAction;

public class TextFontFamilyAction implements ExternalRenderAction<String> {

    private String fontFamily;

    public TextFontFamilyAction(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public String process(String data) {
        Document document = Jsoup.parse(data);
        Element style = document.selectFirst("style");
        style.text(style.html().replaceAll("font-family:\"[\\S]+\";","font-family:\"" + fontFamily + "\";"));
        return document.html();
    }
}

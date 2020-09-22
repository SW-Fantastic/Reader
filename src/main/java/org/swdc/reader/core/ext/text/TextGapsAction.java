package org.swdc.reader.core.ext.text;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.swdc.reader.core.ext.ExternalRenderAction;

public class TextGapsAction implements ExternalRenderAction<String> {

    @Override
    public String process(String data) {
        Document document = Jsoup.parse(data);
        Element style = document.head().appendElement("style");
        StringBuilder sb = new StringBuilder();
        sb.append("body div:first-child{")
                .append("-webkit-column-count:2;")
                .append(" -webkit-column-gap:20px;")
                .append(" -webkit-column-rule:2px dotted #ccc;")
                .append(" }");
        style.text(sb.toString());
        return document.html();
    }

}

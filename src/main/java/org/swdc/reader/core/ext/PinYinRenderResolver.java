package org.swdc.reader.core.ext;

import jakarta.inject.Inject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.locators.*;

/**
 * 拼音渲染器。
 *
 * 开启显示拼音后，这里渲染拼音
 */
@MultipleImplement(RenderResolver.class)
public class PinYinRenderResolver extends AbstractResolver {

    @Inject
    private TextConfig config;

    @Inject
    private Logger logger;

    @Override
    public boolean support(Class<? extends BookLocator> clazz) {
        return config.getShowPinYin() &&
                (clazz == TextLocator.class ||
                clazz == EpubLocator.class ||
                clazz == MobiLocator.class ||
                clazz == UMDLocator.class);
    }

    @Override
    public void renderStyle(StringBuilder builder) {
        builder.append("ruby{ ")
                .append("padding:").append(config.getFontSize() * 0.5).append("px;")
                .append("font-size: ").append(config.getFontSize() + "px;")
                .append("}")
                .append("ruby rt {")
                .append("font-size: ").append(config.getFontSize() * 0.9).append("px;")
                .append("}");
    }

    @Override
    public void renderContent(Element document) {
        try {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
            format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

            if (document != null) {
                if (document instanceof Document) {
                    document = ((Document)document).body();
                }
                Elements elements = document.getElementsByTag("p");
                for (Element textNode : elements) {
                    String data = textNode.text();
                    StringBuilder line = new StringBuilder();
                    for (Character ch : data.toCharArray()) {
                        if (String.valueOf(ch).matches("[0-9aA-zZ()\\-+*/]")) {
                            line.append("<ruby>").append(ch).append("</ruby>");
                            continue;
                        }
                        line.append("<ruby>").append(ch).append("<rt>").append(PinyinHelper.toHanYuPinyinString(ch+"", format," ", true)).append("</rt></ruby>");
                    }
                    textNode.html(line.toString());
                }
            }
        } catch (Exception ex) {
            logger.error("渲染失败：",ex);
        }
    }
}

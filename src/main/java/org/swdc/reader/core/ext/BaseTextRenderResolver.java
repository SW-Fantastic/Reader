package org.swdc.reader.core.ext;

import lombok.extern.apachecommons.CommonsLog;
import org.jsoup.nodes.Element;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.locators.EpubLocator;
import org.swdc.reader.core.locators.MobiLocator;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.services.CommonComponents;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

/**
 * 基础文本渲染器
 * 文本类型的数据在这里渲染样式
 */
@CommonsLog
public class BaseTextRenderResolver extends AbstractResolver {

    @Aware
    private TextConfig textConfig;

    @Aware
    private EpubConfig epubConfig;

    @Aware
    private CommonComponents commonComponents;

    private String backgroundImageData;

    @Override
    public void initialize() {
        try {
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            DataInputStream backgroundInput = new DataInputStream(new FileInputStream(new File(getAssetsPath() + "/readerBackground/" + textConfig.getBackgroundImage())));
            byte[] data = new byte[1024];
            while (backgroundInput.read(data) != -1) {
                bot.write(data);
            }
            bot.flush();
            backgroundInput.close();
            backgroundImageData = Base64.getEncoder().encodeToString(bot.toByteArray());
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    @Override
    public boolean support(Class<? extends BookLocator> clazz) {
        return (clazz == TextLocator.class ||
                clazz == EpubLocator.class ||
                clazz == MobiLocator.class);
    }

    @Override
    public void renderStyle(StringBuilder builder) {
        builder.append("body {")
                .append("font-family:\"")
                .append(commonComponents.getFontFamily(textConfig.getFontPath()) != null ? commonComponents.getFontFamily(textConfig.getFontPath()):"Microsoft YaHei").append("\";")
                .append("font-color:").append(textConfig.getFontColor()).append(";")
                .append("font-size:").append(textConfig.getFontSize()).append("px;")
                .append("background-color:").append(textConfig.getBackgroundColor()).append(";")
                .append("overflow-wrap: break-word;")
                .append("word-wrap: break-word;")
                .append("-webkit-font-smoothing: antialiased;")
                .append("padding: 18px;");
        if (textConfig.getEnableBackgroundImage()) {
            builder.append("background-image: url(data:image/png;base64,").append(backgroundImageData).append(");");
        }
        builder.append("}")
                .append("img {")
                .append("max-width: 100%;")
                .append("}")
                .append("a {")
                .append("text-decoration: none;")
                .append("color:").append(epubConfig.getLinkColor()).append(";")
                .append("}")
                .append("li {")
                .append("padding: 12px;")
                .append("}");
        if (textConfig.getEnableBackgroundImage()) {
            builder.append("body div:first-child{")
                    .append("background-color: rgba(255,255,255,0.8);")
                    .append("padding: 36px;")
                    .append("}");
        } else {
            builder.append("body div:first-child{")
                    .append("padding: 24px")
                    .append("}");
        }
        builder.append("p {")
                .append("line-height: 2;")
                .append("text-indent: ").append(textConfig.getFontSize() *2 + "px;")
                .append("letter-spacing: 1.2px;");
        if (textConfig.getEnableTextShadow()){
            builder.append("text-shadow: 0px 0px 5px ").append(textConfig.getShadowColor()).append(";");
        }
        builder.append("}");
    }

    @Override
    public void renderContent(Element document) {

    }
}

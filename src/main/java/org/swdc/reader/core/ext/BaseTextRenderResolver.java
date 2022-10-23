package org.swdc.reader.core.ext;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.scene.paint.Color;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.locators.*;
import org.swdc.reader.events.ConfigChangedEvent;
import org.swdc.reader.services.HelperServices;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.util.Base64;

/**
 * 基础文本渲染器
 * 文本类型的数据在这里渲染样式
 */
@MultipleImplement(RenderResolver.class)
public class BaseTextRenderResolver extends AbstractResolver {

    @Inject
    private TextConfig textConfig;

    @Inject
    private EpubConfig epubConfig;

    @Inject
    private String backgroundImageData;

    @Inject
    private HelperServices helperServices;

    @Inject
    private Logger logger;

    @PostConstruct
    public void initialize() {
        try {
            if (textConfig.getEnableBackgroundImage()) {
                ByteArrayOutputStream bot = new ByteArrayOutputStream();
                DataInputStream backgroundInput = new DataInputStream(
                        Files.newInputStream(helperServices.getAssetsFolder()
                                .toPath()
                                .resolve("pageBg")
                                .resolve(textConfig.getBackgroundImage())));

                byte[] data = new byte[1024];
                while (backgroundInput.read(data) != -1) {
                    bot.write(data);
                }
                bot.flush();
                backgroundInput.close();
                backgroundImageData = Base64.getEncoder().encodeToString(bot.toByteArray());
            }
        } catch (Exception ex) {
            logger.error("载入失败",ex);
        }
    }

    @EventListener(type = ConfigChangedEvent.class)
    public void resourceReload(ConfigChangedEvent event) {
        this.backgroundImageData = null;
        this.initialize();
    }

    @Override
    public boolean support(Class<? extends BookLocator> clazz) {
        return (clazz == TextLocator.class ||
                clazz == EpubLocator.class ||
                clazz == MobiLocator.class ||
                clazz == UMDLocator.class );
    }

    @Override
    public void renderStyle(StringBuilder builder) {

        String fontFamily = "Microsoft YaHei";
        if (textConfig.getFontFileName() != null && !textConfig.getFontFileName().isEmpty()) {
            fontFamily = helperServices.getFontFamily(textConfig.getFontFileName());
        }

        builder.append("body {")
                .append("font-family:\"")
                .append(fontFamily).append("\";")
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
        builder.append("div,p {")
                .append("line-height: 2.5;")
                .append("text-indent: ").append(textConfig.getFontSize() *2 + "px;")
                .append("letter-spacing: 1.2px;");
        if (textConfig.getEnableTextShadow()){
            builder.append("text-shadow: 0px 0px 7px ").append(textConfig.getShadowColor()).append(";");
        }
        builder.append("}");

    }

    @Override
    public void renderContent(Element document) {

    }
}

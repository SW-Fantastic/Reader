package org.swdc.reader.core.ext;

import org.jsoup.nodes.Element;
import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.reader.core.locators.BookLocator;

/**
 * 渲染器接口
 *
 * 根据配置渲染style或者content。
 */
@ImplementBy({ BaseTextRenderResolver.class, PinYinRenderResolver.class })
public interface RenderResolver {

    boolean support(Class<? extends BookLocator> clazz);

    void renderStyle(StringBuilder builder);

    void renderContent(Element document);

}

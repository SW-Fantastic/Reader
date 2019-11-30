package org.swdc.reader.core;

import org.jsoup.nodes.Element;

/**
 * 渲染器接口
 *
 * 根据配置渲染style或者content。
 */
public interface RenderResolver {

    boolean support(Class<? extends BookLocator> clazz);

    void renderStyle(StringBuilder builder);

    void renderContent(Element document);

}

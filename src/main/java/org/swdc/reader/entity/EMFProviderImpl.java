package org.swdc.reader.entity;

import org.swdc.data.EMFProvider;

import java.util.Arrays;
import java.util.List;

/**
 * 实现entityProvider，
 * 注册Entities类。
 */
public class EMFProviderImpl extends EMFProvider {

    @Override
    public List<Class> registerEntities() {
        return Arrays.asList(
            Book.class,
            BookMark.class,
            BookTag.class,
            BookType.class,
            ContentsItem.class
        );
    }

}

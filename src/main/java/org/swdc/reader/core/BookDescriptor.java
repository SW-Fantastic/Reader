package org.swdc.reader.core;

import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.reader.core.readers.*;
import org.swdc.reader.entity.Book;

import java.io.File;

/**
 * 这个类是描述支持的文件类型的组件类
 * 他提供支持的文件类型以及对应的Reader对象。
 */
@ImplementBy(value = {
        TextBookDescriptor.class,
        EpubBookDescriptor.class,
        PDFBookDescriptor.class,
        ChmBookDescriptor.class,
        MobiBookDescriptor.class})
public interface BookDescriptor {

    boolean support(Book file);

    boolean support(File file);

    <T extends BookReader> T createReader(Book book);

    FileChooser.ExtensionFilter getFilter();

}

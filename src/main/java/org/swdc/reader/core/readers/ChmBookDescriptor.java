package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;

import java.io.File;

@MultipleImplement(BookDescriptor.class)
public class ChmBookDescriptor implements BookDescriptor {

    private FileChooser.ExtensionFilter filter;

    @Inject
    private HelperServices helperServices;

    @Override
    public boolean support(Book file) {
        return file.getName().toLowerCase().endsWith("chm");
    }

    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith("chm");
    }

    @Override
    public ChmBookReader createReader(Book book) {
        return new ChmBookReader.Builder()
                .book(book)
                .assetsFolder(helperServices.getAssetsFolder())
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            filter = new FileChooser.ExtensionFilter("CHM文档","*.chm");
        }
        return filter;
    }

}

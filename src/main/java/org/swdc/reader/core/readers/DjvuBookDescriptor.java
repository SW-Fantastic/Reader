package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;

@MultipleImplement(BookDescriptor.class)
public class DjvuBookDescriptor implements BookDescriptor {

    @Inject
    private HelperServices helperServices;

    @Inject
    private List<RenderResolver> resolvers;

    @Inject
    private TOCAndFavoriteDialog tocDialog;

    private FileChooser.ExtensionFilter filter;

    @Override
    public boolean support(Book file) {
        return file.getName().toLowerCase().endsWith("djvu");
    }

    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith("djvu");
    }

    @Override
    public DjvuBookReader createReader(Book book) {
        return new DjvuBookReader.Builder()
                .book(book)
                .assetsFolder(helperServices.getAssetsFolder())
                .resolvers(resolvers)
                .executor(helperServices.getExecutor())
                .tocDialog(tocDialog)
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            filter = new FileChooser.ExtensionFilter("Scanned Document Djvu","*.djvu");
        }
        return filter;
    }
}

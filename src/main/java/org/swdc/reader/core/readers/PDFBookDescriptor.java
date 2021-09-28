package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;

@MultipleImplement(BookDescriptor.class)
public class PDFBookDescriptor implements BookDescriptor {

    @Inject
    private PDFConfig config;

    @Inject
    private HelperServices helperServices;

    @Inject
    private TOCAndFavoriteDialog dialog;

    @Override
    public boolean support(Book target) {
        return target.getMimeData().equals("application/pdf") &&
                target.getName().toLowerCase().endsWith("pdf");
    }

    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith("pdf");
    }

    @Override
    public PDFBookReader createReader(Book book) {
        PDFBookReader.Builder builder = new PDFBookReader.Builder();
        return builder.book(book)
                .config(config)
                .assetsFolder(helperServices.getAssetsFolder())
                .executor(helperServices.getExecutor())
                .tocDialog(dialog).build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        return new FileChooser.ExtensionFilter("Adobe PDF","*.pdf");
    }
}

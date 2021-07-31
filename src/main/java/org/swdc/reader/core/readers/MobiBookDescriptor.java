package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.util.List;

@MultipleImplement(BookDescriptor.class)
public class MobiBookDescriptor implements BookDescriptor {

    private FileChooser.ExtensionFilter filter;

    @Inject
    private TextConfig textConfig;

    @Inject
    private List<RenderResolver> resolvers;

    @Inject
    private HelperServices helperServices;

    @Inject
    private TOCAndFavoriteDialog tocAndFavoriteDialog;

    @Override
    public boolean support(Book file) {
        return file.getName().toLowerCase().endsWith("mobi");
    }

    @Override
    public MobiBookReader createReader(Book book) {
        MobiBookReader.Builder builder = new MobiBookReader.Builder();
        return builder.book(book)
                .config(textConfig)
                .assetsFolder(helperServices.getAssetsFolder())
                .executor(helperServices.getExecutor())
                .resolvers(resolvers)
                .dialog(tocAndFavoriteDialog)
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            filter = new FileChooser.ExtensionFilter("Kindle文档","*.mobi");
        }
        return filter;
    }

}

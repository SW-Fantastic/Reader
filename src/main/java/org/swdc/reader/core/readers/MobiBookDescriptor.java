package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.fx.FXResources;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.LanguageKeys;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

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

    @Inject
    private FXResources resources;

    @Override
    public boolean support(Book file) {
        return file.getName().toLowerCase().endsWith("mobi");
    }

    @Override
    public boolean support(File file) {
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
                .bundle(resources.getResourceBundle())
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            ResourceBundle bundle = resources.getResourceBundle();
            filter = new FileChooser.ExtensionFilter(bundle.getString(LanguageKeys.KEY_MOBI_FORMAT),"*.mobi");
        }
        return filter;
    }

}

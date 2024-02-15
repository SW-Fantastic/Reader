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
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

@MultipleImplement(BookDescriptor.class)
public class TextBookDescriptor implements BookDescriptor {

    private FileChooser.ExtensionFilter filter = null;

    @Inject
    private HelperServices helperServices;

    @Inject
    private List<RenderResolver> resolvers;

    @Inject
    private TOCAndFavoriteDialog tocDialog;

    @Inject
    private FXResources resources;

    @Override
    public boolean support(Book target) {
        if (target.getName().toLowerCase().endsWith("txt")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean support(File target) {
        if (target.getName().toLowerCase().endsWith("txt")) {
            return true;
        }
        return false;
    }

    @Override
    public TextBookReader createReader(Book book) {
        TextBookReader.Builder builder = new TextBookReader.Builder();
        return builder.book(book)
                .codeDet(helperServices.getCodepageDetectorProxy())
                .resolvers(resolvers)
                .assetsFolder(helperServices.getAssetsFolder())
                .tocDialog(tocDialog)
                .exec(helperServices.getExecutor())
                .bundle(resources.getResourceBundle())
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            ResourceBundle bundle = resources.getResourceBundle();
            filter = new FileChooser.ExtensionFilter(bundle.getString(
                    LanguageKeys.KEY_TXT_FORMAT
            ),"*.txt");
        }
        return filter;
    }
}

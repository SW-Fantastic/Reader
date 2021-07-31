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

import java.nio.file.Path;
import java.util.List;

@MultipleImplement(BookDescriptor.class)
public class TextBookDescriptor implements BookDescriptor {

    private FileChooser.ExtensionFilter filter = null;

    @Inject
    private HelperServices helperServices;

    @Inject
    private TextConfig textConfig;

    @Inject
    private List<RenderResolver> resolvers;

    @Inject
    private TOCAndFavoriteDialog tocDialog;

    @Override
    public boolean support(Book target) {
        if (target.getName().toLowerCase().endsWith("txt") && target.getMimeData().toLowerCase().equals("text/plain")) {
            return true;
        }
        return false;
    }

    @Override
    public TextBookReader createReader(Book book) {
        TextBookReader.Builder builder = new TextBookReader.Builder();
        return builder.book(book)
                .codeDet(helperServices.getCodepageDetectorProxy())
                .config(textConfig)
                .resolvers(resolvers)
                .assetsFolder(helperServices.getAssetsFolder())
                .tocDialog(tocDialog)
                .exec(helperServices.getExecutor())
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (filter == null) {
            filter = new FileChooser.ExtensionFilter("文本格式","*.txt");
        }
        return filter;
    }
}

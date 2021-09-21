package org.swdc.reader.core.readers;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.HelperServices;
import org.swdc.reader.ui.dialogs.reader.TOCAndFavoriteDialog;

import java.util.List;

@MultipleImplement(BookDescriptor.class)
public class EpubBookDescriptor implements BookDescriptor {

    private FileChooser.ExtensionFilter filter = null;

    @Inject
    private TextConfig textConfig;

    @Inject
    private EpubConfig epubConfig;

    @Inject
    private List<RenderResolver> resolvers;

    @Inject
    private HelperServices helperServices;

    @Inject
    private TOCAndFavoriteDialog tocAndFavoriteDialog;

    @Override
    public boolean support(Book file) {
        return file.getName()
                .toLowerCase()
                .endsWith("epub") &&
                file.getMimeData()
                        .equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }


    @Override
    public EpubBookReader createReader(Book book) {
        EpubBookReader.Builder builder = new EpubBookReader.Builder();
        return builder.book(book)
                .codeDet(helperServices.getCodepageDetectorProxy())
                .config(textConfig,epubConfig)
                .resolvers(resolvers)
                .assetsFolder(helperServices.getAssetsFolder())
                .tocDialog(tocAndFavoriteDialog)
                .executor(helperServices.getExecutor())
                .build();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        if (this.filter == null) {
            this.filter = new FileChooser.ExtensionFilter("Epub电子出版格式","*.epub");
        }
        return this.filter;
    }

}

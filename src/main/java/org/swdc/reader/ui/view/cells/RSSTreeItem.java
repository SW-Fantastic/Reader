package org.swdc.reader.ui.view.cells;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.nio.file.Path;

public class RSSTreeItem {

    private File rssArchive;

    private String path;

    public RSSTreeItem(File archive) {
        this.rssArchive = archive;
    }

    public RSSTreeItem(String item) {
        this.path = item;
    }

    public String getPath() {
        return path;
    }

    public File getRssArchive() {
        return rssArchive;
    }

    @Override
    public String toString() {
        return rssArchive != null ? rssArchive.getName().split("[.]")[0] : path;
    }
}

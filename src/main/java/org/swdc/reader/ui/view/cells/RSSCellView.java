package org.swdc.reader.ui.view.cells;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.reader.services.CommonComponents;
import org.swdc.reader.ui.controller.RSSData;

import java.util.ArrayList;
import java.util.List;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class RSSCellView extends FXView {

    private RSSData rssData;

    public void setRssData(RSSData rssData) {
        Label title = findById("title");
        Label date = findById("date");
        Label content = findById("content");

        title.setText(rssData.getTitle());
        date.setText(rssData.getDate());
        content.setText(rssData.getDesc());
        this.rssData = rssData;
    }
}

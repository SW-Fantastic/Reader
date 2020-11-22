package org.swdc.reader.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;

@View(stage = false)
public class RSSView extends FXView {

    @Aware
    private FontawsomeService fontawsomeService = null;

    @Override
    public void initialize() {
        configButton("rssAdd","plus");
        configButton("editRss","pencil");
        configButton("refresh","refresh");
        configButton("delete","trash");
    }

    private void configButton(String btnId,String iconName) {
        Button btn = this.findById(btnId);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(fontawsomeService.getFont(FontSize.SMALL));
        btn.setText(fontawsomeService.getFontIcon(iconName));
    }

}

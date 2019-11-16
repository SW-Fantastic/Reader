package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;


/**
 * Created by lenovo on 2019/5/19.
 */
public class SplashView extends SplashScreen {

    @Override
    public String getImagePath() {
        return "/images/title.png";
    }

    @Override
    public Parent getParent() {
        Group gp = new Group();
        ImageView imageView = new ImageView(this.getClass().getResource(this.getImagePath()).toExternalForm());
        gp.getChildren().add(imageView);
        return gp;
    }

}

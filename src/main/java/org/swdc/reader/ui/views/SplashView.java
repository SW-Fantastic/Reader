package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


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
        ClassPathResource resource = new ClassPathResource(getImagePath());
        try {
            Image image = new Image(resource.getInputStream());
            Group gp = new Group();
            ImageView imageView = new ImageView(image);
            gp.getChildren().add(imageView);
            return gp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

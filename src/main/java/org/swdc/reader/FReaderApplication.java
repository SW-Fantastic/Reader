package org.swdc.reader;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.ui.views.ReaderView;
import org.swdc.reader.ui.views.SplashView;

@SpringBootApplication
public class FReaderApplication extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		launch(FReaderApplication.class, ReaderView.class,new SplashView(), args);
	}

	@Override
	public void beforeShowingSplash(Stage splashStage) {
		splashStage.getIcons().addAll(AwsomeIconData.getImageIcons());
	}

	@Override
	public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
		stage.getIcons().addAll(AwsomeIconData.getImageIcons());
		stage.setMinHeight(630);
		stage.setMinWidth(1000);
		stage.setTitle("幻想藏书阁");
	}
}

package org.swdc.reader;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.swdc.reader.event.RestartEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.ui.views.ReaderView;
import org.swdc.reader.ui.views.SplashView;

import java.io.File;
import java.net.MalformedURLException;

@EnableAsync
@CommonsLog
@SpringBootApplication
public class FReaderApplication extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		launch(FReaderApplication.class, ReaderView.class,new SplashView(), args);
	}

	@Override
	public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
		stage.getIcons().addAll(AwsomeIconData.getImageIcons());
		stage.setMinHeight(680);
		stage.setMinWidth(1000);
		stage.setTitle("幻想藏书阁");
		stage.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue && GUIState.getScene().getStylesheets().size() == 0) {
				ApplicationConfig config = ctx.getBean(ApplicationConfig.class);
				try {
					GUIState.getScene().getStylesheets().add(new File("./configs/theme/" + config.getTheme() + "/stage.css").toURI().toURL().toExternalForm());
				} catch (MalformedURLException e) {
					log.error(e);
				}
			}
		});
		stage.setOnCloseRequest(event -> {
			stage.hide();
			ctx.stop();
			ctx.close();
			System.exit(0);
		});
	}

	@EventListener(RestartEvent.class)
	public void onRestart() {
		Platform.runLater(() -> {
			try {
				GUIState.getStage().close();
				this.stop();
				this.init();
				this.start(new Stage());
			} catch (Exception e){
				log.error(e);
			}
		});
	}

	@Override
	public void beforeShowingSplash(Stage splashStage) {
		splashStage.getIcons().addAll(AwsomeIconData.getImageIcons());
	}
}

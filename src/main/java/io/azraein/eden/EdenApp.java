package io.azraein.eden;

import java.util.HashMap;
import java.util.Map;

import io.azraein.eden.logic.Eden;
import io.azraein.eden.nodes.scenes.EdenDesktop;
import io.azraein.eden.nodes.scenes.EdenLobby;
import io.azraein.eden.nodes.scenes.EdenMessaging;
import io.azraein.eden.nodes.scenes.EdenNews;
import io.azraein.eden.nodes.scenes.EdenScene;
import io.azraein.eden.nodes.scenes.EdenSettingsScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EdenApp extends Application {

	private Stage edenAppStage;
	private Eden eden;

	private final Map<String, EdenScene> EDEN_SCENES = new HashMap<>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		eden = new Eden();

		EdenLobby lobby = new EdenLobby(this);
		EdenMessaging messaging = new EdenMessaging(this);
		EdenNews news = new EdenNews(this);
		EdenSettingsScene settings = new EdenSettingsScene(this);
		EdenDesktop desktop = new EdenDesktop(this);

		EDEN_SCENES.put("desktop", desktop);
		EDEN_SCENES.put("lobby", lobby);
		EDEN_SCENES.put("messaging", messaging);
		EDEN_SCENES.put("news", news);
		EDEN_SCENES.put("settings", settings);

		eden.getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {
			// If the User ever logs out, send the user back to the lobby
			if (newValue == null) {
				setScene(EDEN_SCENES.get("lobby"));
			}
		});

		edenAppStage = primaryStage;

		primaryStage.setScene(new Scene(EDEN_SCENES.get("lobby").getRootPane(), 1280, 720));
		primaryStage.setTitle("AnzelElectronics Eden");
		primaryStage.show();
	}

	public void setScene(EdenScene scene) {
		edenAppStage.getScene().setRoot(scene.getRootPane());
	}

	public Eden getEden() {
		return eden;
	}

	public Stage getEdenAppStage() {
		return edenAppStage;
	}

	public Map<String, EdenScene> getEdenScenes() {
		return EDEN_SCENES;
	}

}

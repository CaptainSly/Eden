package io.azraein.eden;

import java.util.HashMap;
import java.util.Map;

import io.azraein.eden.logic.eden.Eden;
import io.azraein.eden.nodes.scenes.EdenDesktop;
import io.azraein.eden.nodes.scenes.EdenLobby;
import io.azraein.eden.nodes.scenes.EdenMessaging;
import io.azraein.eden.nodes.scenes.EdenNews;
import io.azraein.eden.nodes.scenes.EdenScene;
import io.azraein.eden.nodes.scenes.EdenSettingsScene;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class EdenApp extends Application {

	private Stage edenAppStage;
	private Eden eden;
	private final ObjectProperty<EdenScene> edenSceneProperty = new SimpleObjectProperty<>();
	private final Map<String, EdenScene> EDEN_SCENES = new HashMap<>();

	// TODO: Create Custom StyleSheets for Eden

	@Override
	public void start(Stage primaryStage) throws Exception {
		eden = new Eden();
		edenAppStage = primaryStage;

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

		primaryStage.setScene(new Scene(new Region(), 1280, 720));
		primaryStage.setTitle("AnzelElectronics Eden");
		primaryStage.show();

		primaryStage.setOnCloseRequest(e -> {
			// TODO: If anything needs to be deallocated in one way or another do so here
			// before the program closes
			eden.getEdensMouth().stopTalking();
		});

		setScene(EDEN_SCENES.get("lobby"));
	}

	public void setScene(EdenScene scene) {
		edenAppStage.getScene().setRoot(scene.getRootPane());
		edenSceneProperty.set(scene);
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

	public ObjectProperty<EdenScene> getEdenSceneProperty() {
		return edenSceneProperty;
	}

}

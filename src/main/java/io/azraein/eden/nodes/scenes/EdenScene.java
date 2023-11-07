package io.azraein.eden.nodes.scenes;

import org.controlsfx.control.StatusBar;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.eden.Eden;
import io.azraein.eden.nodes.EdenTaskbar;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public abstract class EdenScene {

	protected Eden edenSceneLogic;
	protected EdenApp edenSceneView;

	protected EdenTaskbar taskbar;
	protected StatusBar statusbar;
	protected BorderPane rootPane;

	public EdenScene(EdenApp edenSceneView) {
		this.edenSceneView = edenSceneView;
		edenSceneLogic = edenSceneView.getEden();
		rootPane = new BorderPane();
		taskbar = new EdenTaskbar(edenSceneView);
		
		statusbar = new StatusBar();

		edenSceneLogic.getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {
			if (newValue == null)
				statusbar.setText("Goodbye " + oldValue.getUserName());
		});
		
		// Add a Lobby MenuItem to the taskbar just incase the user would like to return there without logging out. 
		MenuItem edenSceneLobbyItem = new MenuItem("Eden Lobby", FontIcon.of(Material2AL.HOME, 32));
		edenSceneLobbyItem.setOnAction(event -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("lobby")));
		
		edenSceneView.getEdenSceneProperty().addListener((obs, oldValue, newValue) -> {
			Menu sceneMenu = taskbar.getMenus().get(1);
			
			if (!(newValue instanceof EdenLobby))
				sceneMenu.getItems().add(edenSceneLobbyItem);
			else
				sceneMenu.getItems().remove(edenSceneLobbyItem);
		
		});
		
		rootPane.setTop(taskbar);
		rootPane.setBottom(statusbar);
	}
	
	protected void setContent(Node content) {
		rootPane.setCenter(content);
	}

	public BorderPane getRootPane() {
		return rootPane;
	}

	public StatusBar getStatusBar() {
		return statusbar;
	}

}

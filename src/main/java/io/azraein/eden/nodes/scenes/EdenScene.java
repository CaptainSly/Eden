package io.azraein.eden.nodes.scenes;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.Eden;
import javafx.scene.layout.BorderPane;

public abstract class EdenScene {

	protected Eden edenSceneLogic;
	protected EdenApp edenSceneView;
	protected BorderPane rootPane;

	public EdenScene(EdenApp edenSceneView) {
		this.edenSceneView = edenSceneView;
		edenSceneLogic = edenSceneView.getEden();
		rootPane = new BorderPane();
	}

	public BorderPane getRootPane() {
		return rootPane;
	}
	
}

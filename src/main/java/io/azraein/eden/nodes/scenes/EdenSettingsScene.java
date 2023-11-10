package io.azraein.eden.nodes.scenes;

import java.util.Random;

import org.tinylog.Logger;

import io.azraein.eden.EdenApp;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class EdenSettingsScene extends EdenScene {

	public EdenSettingsScene(EdenApp edenSceneView) {
		super(edenSceneView);

		// Setting Nodes
		Label edenToggleLbl = new Label("Use Eden"); // Eden Toggle Dummy Control
		CheckBox edenToggle = new CheckBox();
		edenToggle.setDisable(true);
		edenToggle.setSelected(true);

		edenToggleLbl.setOnMouseClicked(e -> {
			Random rnJesus = new Random(System.currentTimeMillis());
			int randomChance = rnJesus.nextInt(69);

			Logger.info("RandomChance: " + randomChance);
			
			if (randomChance == 42)
				edenSceneView.getEden().speak("Why must you keep trying?");
		});

		// Control Panes
		GridPane settingsGrid = new GridPane();
		settingsGrid.setPadding(new Insets(10, 10, 10, 10));
		settingsGrid.setHgap(10);
		settingsGrid.setVgap(10);

		// Add to Controls
		settingsGrid.add(edenToggleLbl, 0, 0);
		settingsGrid.add(edenToggle, 1, 0);

		// Set main content
		setContent(settingsGrid);

	}

}

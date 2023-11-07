package io.azraein.eden.nodes.scenes;

import io.azraein.eden.EdenApp;
import io.azraein.eden.nodes.EdenDesktopPane;

public class EdenDesktop extends EdenScene {

	private EdenDesktopPane desktopPane;

	public EdenDesktop(EdenApp edenSceneView) {
		super(edenSceneView);
		desktopPane = new EdenDesktopPane(edenSceneView);
		setContent(desktopPane.getEdenDesktopPane());
	}

}

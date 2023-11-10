package io.azraein.eden.nodes.scenes;

import io.azraein.eden.EdenApp;
import io.azraein.eden.nodes.tabs.EdenEmail;
import io.azraein.eden.nodes.tabs.EdenInstantMessaging;
import javafx.scene.control.TabPane;

public class EdenMessaging extends EdenScene {

	private TabPane edenMessagerTabPane;

	public EdenMessaging(EdenApp edenApp) {
		super(edenApp);
		edenMessagerTabPane = new TabPane();
		EdenEmail emailTab = new EdenEmail();
		EdenInstantMessaging imTab = new EdenInstantMessaging(edenApp);

		edenMessagerTabPane.getTabs().addAll(imTab, emailTab);
		setContent(edenMessagerTabPane);
	}

}

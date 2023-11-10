package io.azraein.eden.nodes.tabs;

import io.azraein.eden.EdenApp;
import io.azraein.eden.nodes.chat.EdenMessageBox;
import javafx.scene.control.Tab;

public class EdenInstantMessaging extends Tab {

	public EdenInstantMessaging(EdenApp edenApp) {
		this.setText("Eden InstantMessaging");
		this.setClosable(false);
		
		EdenMessageBox edenMessageBox = new EdenMessageBox(edenApp);
		setContent(edenMessageBox);
	}

}

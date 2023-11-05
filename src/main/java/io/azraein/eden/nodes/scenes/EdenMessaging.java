package io.azraein.eden.nodes.scenes;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;

import io.azraein.eden.EdenApp;
import io.azraein.eden.nodes.tabs.EdenEmail;
import io.azraein.eden.nodes.tabs.EdenInstantMessaging;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

public class EdenMessaging extends EdenScene {

	private TabPane edenMessagerTabPane;

	public EdenMessaging(EdenApp edenSceneView) {
		super(edenSceneView);
		edenMessagerTabPane = new TabPane();
		EdenEmail emailTab = new EdenEmail();
		EdenInstantMessaging imTab = new EdenInstantMessaging();
		MenuBar menuBar = new MenuBar();
		MenuItem logOut = new MenuItem("Logout of Eden");

		FontIcon logoutIcon = FontIcon.of(Material2RoundAL.LOG_OUT);
		logoutIcon.setIconSize(32);

		FontIcon accountCenterIcon = FontIcon.of(Material2RoundAL.ACCOUNT_CIRCLE);
		accountCenterIcon.setIconSize(32);

		Menu accountCenter = new Menu("Account Center", accountCenterIcon);
		accountCenter.getItems().add(logOut);

		logOut.setGraphic(logoutIcon);
		logOut.setOnAction(event -> edenSceneLogic.setCurrentLoggedInUser(null));
		menuBar.getMenus().add(accountCenter);

		edenMessagerTabPane.getTabs().addAll(emailTab, imTab);
		rootPane.setTop(menuBar);
		rootPane.setCenter(edenMessagerTabPane);
	}

}

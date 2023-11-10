package io.azraein.eden.nodes;

import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.tinylog.Logger;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.EdenUser;
import io.azraein.eden.utils.EdenUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class EdenTaskbar extends MenuBar {

	public EdenTaskbar(EdenApp edenApp) {
		// Create Account Center

		Menu accountCenterMenu = new Menu("Account Center", FontIcon.of(Material2AL.ACCOUNT_CIRCLE, 32));

		// Account Center Menu Items
		MenuItem accountLoginItem = new MenuItem("Login to Eden", FontIcon.of(Material2AL.LOG_IN, 32));
		MenuItem accountLogoutItem = new MenuItem("Logout of Eden", FontIcon.of(Material2AL.LOG_OUT, 32));

		accountLoginItem.setOnAction(event -> {

			// Create a Dialog that lets the user log in
			Dialog<Pair<String, String>> logInDialog = new Dialog<>();
			logInDialog.setTitle("Login to Eden");
			logInDialog.setHeaderText("Login to your Eden Account");

			FontIcon lockIcon = FontIcon.of(Material2RoundAL.LOCK);
			lockIcon.setIconSize(64);

			logInDialog.setGraphic(lockIcon);

			// Set Button Types
			ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
			logInDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			TextField username = new TextField();
			username.setPromptText("Username");

			PasswordField password = new PasswordField();
			password.setPromptText("Password");

			grid.add(new Label("Username:"), 0, 0);
			grid.add(username, 1, 0);
			grid.add(new Label("Password:"), 0, 1);
			grid.add(password, 1, 1);

			Node loginButton = logInDialog.getDialogPane().lookupButton(loginButtonType);
			loginButton.setDisable(true);

			username.textProperty()
					.addListener((obs, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

			logInDialog.getDialogPane().setContent(grid);
			Platform.runLater(() -> username.requestFocus());

			logInDialog.setResultConverter(dialogButton -> {

				if (dialogButton == loginButtonType)
					return new Pair<>(username.getText(), password.getText());

				return null;
			});

			Optional<Pair<String, String>> result = logInDialog.showAndWait();

			result.ifPresent(userPass -> {
				EdenUser user = edenApp.getEden().getEdenUserFromUsername(userPass.getKey());
				if (user != null) {
					String hashedPassword = EdenUtils.generateEncryptedPassword(userPass.getValue());
					if (user.getUserEncryptedPass().equals(hashedPassword)) {
						edenApp.getEden().setCurrentLoggedInUser(user);
						accountLoginItem.setDisable(true);

						edenApp.setScene(edenApp.getEdenScenes().get("desktop"));
					}

				} else {
					Logger.debug("Couldn't find a user returning null");
					return;
				}
			});

		});

		accountLogoutItem.setOnAction(event -> edenApp.getEden().setCurrentLoggedInUser(null));
		accountLogoutItem.setDisable(true);

		// AppScene Switches
		Menu appSceneMenu = new Menu("Go to App...", FontIcon.of(Material2AL.APPS, 32));

		// AppScene Switch Menu Item
		MenuItem appSceneDesktop = new MenuItem("Go to EdenDesktop", FontIcon.of(Material2AL.DESKTOP_WINDOWS, 32));
		MenuItem appSceneEmail = new MenuItem("Go to EdenMessaging", FontIcon.of(Material2AL.EMAIL, 32));
		MenuItem appSceneNews = new MenuItem("Go to EdenNews", FontIcon.of(Material2AL.ANNOUNCEMENT, 32));
		MenuItem appSceneSettings = new MenuItem("Go to App Settings", FontIcon.of(Material2AL.APP_SETTINGS_ALT, 32));

		appSceneDesktop.setOnAction(event -> edenApp.setScene(edenApp.getEdenScenes().get("desktop")));
		appSceneDesktop.setDisable(true);

		appSceneEmail.setOnAction(event -> edenApp.setScene(edenApp.getEdenScenes().get("messaging")));
		appSceneEmail.setDisable(true);

		appSceneNews.setOnAction(event -> edenApp.setScene(edenApp.getEdenScenes().get("news")));
		appSceneNews.setDisable(true);

		appSceneSettings.setOnAction(event -> edenApp.setScene(edenApp.getEdenScenes().get("settings")));

		// Setup EdenUser Listener
		edenApp.getEden().getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

			if (newValue == null) { // We Logged a user out
				// Enable Login Item
				accountLoginItem.setDisable(false);

				// Disable the following menuitems
				accountLogoutItem.setDisable(true);
				appSceneDesktop.setDisable(true);
				appSceneEmail.setDisable(true);
				appSceneNews.setDisable(true);

				// Erase the currentLogged user
				accountCenterMenu.setText("Account Center");

			} else {
				// Disable Login Item
				accountLoginItem.setDisable(true);

				// Enable the following menuItems
				accountLogoutItem.setDisable(false);
				appSceneDesktop.setDisable(false);
				appSceneEmail.setDisable(false);
				appSceneNews.setDisable(false);

				// Display the currentLoggedin user
				accountCenterMenu.setText(newValue.getUserName());
			}

		});

		// Add items to menus
		accountCenterMenu.getItems().addAll(accountLoginItem, accountLogoutItem);
		appSceneMenu.getItems().addAll(appSceneDesktop, appSceneEmail, appSceneNews, appSceneSettings);

		this.getMenus().addAll(accountCenterMenu, appSceneMenu);
	}

}

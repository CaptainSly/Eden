package io.azraein.eden.nodes.scenes;

import java.util.Optional;

import org.controlsfx.control.StatusBar;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;
import org.tinylog.Logger;

import io.azraein.eden.EdenApp;
import io.azraein.eden.EdenUtils;
import io.azraein.eden.logic.EdenUser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

public class EdenLobby extends EdenScene {

	private StatusBar lobbyStatusBar;

	public EdenLobby(EdenApp edenSceneView) {
		super(edenSceneView);

		createMenuBar();
		createStatusBar();
		createLobbyView();

	}

	private void createMenuBar() {

		// Menu Bar for Logging in
		MenuBar menuBar = new MenuBar();
		MenuItem logIn = new MenuItem("Login to Eden");
		MenuItem logOut = new MenuItem("Logout of Eden");

		FontIcon loginIcon = FontIcon.of(Material2RoundAL.LOG_IN);
		loginIcon.setIconSize(32);

		FontIcon logoutIcon = FontIcon.of(Material2RoundAL.LOG_OUT);
		logoutIcon.setIconSize(32);

		FontIcon accountCenterIcon = FontIcon.of(Material2RoundAL.ACCOUNT_CIRCLE);
		accountCenterIcon.setIconSize(32);

		logIn.setGraphic(loginIcon);
		logIn.setOnAction(e -> {

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

			username.textProperty().addListener((obs, oldValue, newValue) -> {
				loginButton.setDisable(newValue.trim().isEmpty());
			});

			logInDialog.getDialogPane().setContent(grid);
			Platform.runLater(() -> username.requestFocus());

			logInDialog.setResultConverter(dialogButton -> {

				if (dialogButton == loginButtonType) {
					return new Pair<>(username.getText(), password.getText());
				}

				return null;
			});

			Optional<Pair<String, String>> result = logInDialog.showAndWait();

			result.ifPresent(userPass -> {
				EdenUser user = edenSceneLogic.getEdenUserFromUsername(userPass.getKey());
				if (user != null) {
					Logger.debug("Found User: " + user.getUserName());
					Logger.debug("User EncryptedPass: " + user.getUserEncryptedPass());

					String hashedPassword = EdenUtils.generateEncryptedPassword(userPass.getValue());
					if (user.getUserEncryptedPass().equals(hashedPassword)) {
						// Password Match, log them in, and send a signal of sorts.
						// TODO: either attach a listener somewhere when setting the user with a boolean
						// property or do something of the sorts

						edenSceneLogic.setCurrentLoggedInUser(user);
						Logger.debug("Logged in user: " + user.getUserName());
						logIn.setDisable(true);
						logOut.setText("Logout " + user.getUserName() + " of Eden");
					}

				} else {
					Logger.debug("Couldn't find a user returning null");
					return;
				}
			});

		});

		edenSceneLogic.getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {

			if (newValue == null) {
				logOut.setDisable(true);
				logIn.setDisable(false);
				logOut.setText("Logout of Eden");
			} else {
				logOut.setDisable(false);
				logIn.setDisable(true);
			}

		});

		logOut.setGraphic(logoutIcon);
		logOut.setOnAction(e -> {
			edenSceneLogic.setCurrentLoggedInUser(null);
		});

		Menu accountMenu = new Menu("Account Center");
		accountMenu.setGraphic(accountCenterIcon);

		accountMenu.getItems().addAll(logIn, logOut);
		menuBar.getMenus().add(accountMenu);

		rootPane.setTop(menuBar);
	}

	private void createStatusBar() {
		lobbyStatusBar = new StatusBar();

		edenSceneLogic.getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {
			if (newValue == null)
				lobbyStatusBar.setText("Goodbye " + oldValue.getUserName());
		});

		rootPane.setBottom(lobbyStatusBar);
	}

	int charIndex = 0;

	private void createLobbyView() {
		// The 4 Graphics for the Buttons
		FontIcon desktopIcon = FontIcon.of(Material2RoundAL.DESKTOP_WINDOWS);
		desktopIcon.setIconSize(64);

		FontIcon messagesIcon = FontIcon.of(Material2RoundAL.CHAT_BUBBLE);
		messagesIcon.setIconSize(64);

		FontIcon settingsIcon = FontIcon.of(Material2RoundMZ.SETTINGS);
		settingsIcon.setIconSize(64);

		FontIcon newsIcon = FontIcon.of(Material2AL.BUSINESS_CENTER);
		newsIcon.setIconSize(64);

		String buttonStyle = "-fx-font-size: 16px;" + // Adjust the font size as needed
				"-fx-background-radius: 90;"; // Set padding for the buttons

		// Create 4 Buttons | Desktop, Messages, Settings, and News
		Button desktopBtn = new Button(); // Switches to the Desktop Scene
		Button messagesBtn = new Button(); // Switches to the Messages Scene
		Button settingsBtn = new Button(); // Switches to the Settings Scene
		Button newsBtn = new Button(); // Switches to the News Scene

		desktopBtn.setGraphic(desktopIcon);
		desktopBtn.setTooltip(new Tooltip("Go to User Desktop"));
		desktopBtn.setStyle(buttonStyle);
		desktopBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("desktop")));

		messagesBtn.setGraphic(messagesIcon);
		messagesBtn.setTooltip(new Tooltip("Go to User Messages"));
		messagesBtn.setStyle(buttonStyle);
		messagesBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("messaging")));

		settingsBtn.setGraphic(settingsIcon);
		settingsBtn.setTooltip(new Tooltip("Go to APP Settings"));
		settingsBtn.setStyle(buttonStyle);
		settingsBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("settings")));

		newsBtn.setGraphic(newsIcon);
		newsBtn.setTooltip(new Tooltip("Go to Eden News"));
		newsBtn.setStyle(buttonStyle);
		newsBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("news")));

		// Add a listener to the CurrentLoggedInUser to check for button availability
		edenSceneLogic.getCurrentEdenUser().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				desktopBtn.setDisable(false);
				messagesBtn.setDisable(false);
				newsBtn.setDisable(false);

				lobbyStatusBar.setText("Welcome back " + newValue.getUserName() + "!");

			} else {
				desktopBtn.setDisable(true);
				messagesBtn.setDisable(true);
				newsBtn.setDisable(true);
			}
		});

		desktopBtn.setDisable(true);
		messagesBtn.setDisable(true);
		newsBtn.setDisable(true);

		GridPane lobbyGrid = new GridPane();
		lobbyGrid.setPadding(new Insets(10));
		lobbyGrid.setHgap(10);
		lobbyGrid.setVgap(10);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setHalignment(HPos.CENTER);
		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setValignment(VPos.CENTER);

		lobbyGrid.setAlignment(Pos.CENTER);
		lobbyGrid.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints,
				columnConstraints);
		lobbyGrid.getRowConstraints().add(rowConstraints);

		lobbyGrid.add(desktopBtn, 0, 0);
		lobbyGrid.add(messagesBtn, 1, 0);
		lobbyGrid.add(settingsBtn, 2, 0);
		lobbyGrid.add(newsBtn, 3, 0);

		String labelStyle = "-fx-font-size: 48px;" + // Adjust the font size as needed
				"-fx-font-family: 'Arial';" + // Specify the desired font family
				"-fx-text-fill: #4d0803;";
		DropShadow dropShadow = new DropShadow();

		Label welcomeLbl = new Label();
		welcomeLbl.setEffect(dropShadow);
		welcomeLbl.setStyle(labelStyle);

		// Set up the animation
		String welcomeText = "Welcome to Eden";
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
			if (charIndex < welcomeText.length()) {
				welcomeLbl.setText(welcomeLbl.getText() + welcomeText.charAt(charIndex));
				charIndex++;
			}
		}));
		timeline.setCycleCount(welcomeText.length());
		timeline.play();

		VBox midVBox = new VBox();
		midVBox.setPadding(new Insets(50));
		midVBox.setAlignment(Pos.CENTER);

		midVBox.getChildren().addAll(welcomeLbl, lobbyGrid);

		rootPane.setCenter(midVBox);
	}

}

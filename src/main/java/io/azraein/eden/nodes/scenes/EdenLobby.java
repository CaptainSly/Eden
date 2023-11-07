package io.azraein.eden.nodes.scenes;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;

import io.azraein.eden.EdenApp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EdenLobby extends EdenScene {

	public EdenLobby(EdenApp edenSceneView) {
		super(edenSceneView);
		createLobbyView();
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

				statusbar.setText("Welcome back " + newValue.getUserName() + "!");
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

		setContent(midVBox);
	}

}

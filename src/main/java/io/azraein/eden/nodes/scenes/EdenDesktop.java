package io.azraein.eden.nodes.scenes;

import java.io.File;

import org.controlsfx.control.StatusBar;
import org.kordamp.desktoppanefx.scene.layout.DesktopPane;
import org.kordamp.desktoppanefx.scene.layout.InternalWindow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;
import org.tinylog.Logger;

import io.azraein.eden.EdenApp;
import io.azraein.eden.logic.EdenFile;
import io.azraein.eden.logic.EdenFile.EdenFileType;
import io.azraein.eden.logic.EdenFileUtils;
import io.azraein.eden.logic.EdenUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EdenDesktop extends EdenScene {

	private DesktopPane desktopPane;
	private StatusBar desktopStatusBar;
	private VBox desktopDockBox;

	private InternalWindow edenFileBrowser;
	private int internalTextEditorCap = 0;

	public EdenDesktop(EdenApp edenSceneView) {
		super(edenSceneView);

		desktopPane = new DesktopPane();
		desktopPane.setMaxSize(1024, 768);
		edenFileBrowser = new InternalWindow("window-edenFileBrowser", FontIcon.of(Material2AL.FOLDER),
				"Eden FileBrowser", getFileBrowserContent());

		desktopPane.addInternalWindow(edenFileBrowser);

		desktopStatusBar = new StatusBar();

		desktopDockBox = new VBox();
		desktopDockBox.setPadding(new Insets(50));
		desktopDockBox.setSpacing(75);
		createDock();

		HBox windowBtnHBox = new HBox();
		windowBtnHBox.setSpacing(20);

		Button fileBrowserBtn = new Button();
		FontIcon fileBrowserIcon = FontIcon.of(Material2AL.FILE_COPY);
		fileBrowserIcon.setIconSize(16);
		fileBrowserBtn.setGraphic(fileBrowserIcon);
		fileBrowserBtn.setOnAction(e -> {
		});

		windowBtnHBox.getChildren().add(fileBrowserBtn);

		rootPane.setTop(windowBtnHBox);
		rootPane.setBottom(desktopStatusBar);
		rootPane.setLeft(desktopDockBox);
		rootPane.setCenter(desktopPane);
	}

	private void createDock() {
		Button logOutBtn = new Button();
		logOutBtn.setTooltip(new Tooltip("Logout of Eden Account"));

		Button messageBtn = new Button();
		messageBtn.setTooltip(new Tooltip("Access Messages"));

		Button newsBtn = new Button();
		newsBtn.setTooltip(new Tooltip("View EdenNews"));

		Button settingsBtn = new Button();
		settingsBtn.setTooltip(new Tooltip("Edit APP Settings"));

		FontIcon logOutIcon = FontIcon.of(Material2RoundAL.LOG_OUT);
		logOutIcon.setIconSize(32);

		FontIcon messagesIcon = FontIcon.of(Material2RoundAL.CHAT_BUBBLE);
		messagesIcon.setIconSize(32);

		FontIcon settingsIcon = FontIcon.of(Material2RoundMZ.SETTINGS);
		settingsIcon.setIconSize(32);

		FontIcon newsIcon = FontIcon.of(Material2AL.BUSINESS_CENTER);
		newsIcon.setIconSize(32);

		logOutBtn.setGraphic(logOutIcon);
		messageBtn.setGraphic(messagesIcon);
		settingsBtn.setGraphic(settingsIcon);
		newsBtn.setGraphic(newsIcon);

		settingsBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("settings")));
		messageBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("messaging")));
		newsBtn.setOnAction(e -> edenSceneView.setScene(edenSceneView.getEdenScenes().get("news")));
		logOutBtn.setOnAction(e -> edenSceneLogic.setCurrentLoggedInUser(null));

		desktopDockBox.getChildren().addAll(messageBtn, newsBtn, settingsBtn, logOutBtn);
	}

	private BorderPane getFileBrowserContent() {
		BorderPane fbRoot = new BorderPane();
		FlowPane dirFlowPane = new FlowPane();
		ScrollPane scrollpane = new ScrollPane(dirFlowPane);
		dirFlowPane.setPadding(new Insets(25));

		edenSceneLogic.getCurrentEdenUser().addListener(new ChangeListener<EdenUser>() {

			@Override
			public void changed(ObservableValue<? extends EdenUser> observable, EdenUser oldValue, EdenUser newValue) {

				if (newValue == null) {
					dirFlowPane.getChildren().clear();
				} else {
					// It's not null fill the pane
					dirFlowPane.getChildren().clear();
					addEdenFilesToBrowser(dirFlowPane,
							EdenFileUtils.getUserDirectoryFiles(edenSceneLogic.getCurrentLoggedInUser()));
				}

			}
		});

		dirFlowPane.getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(Change<? extends Node> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (Node node : c.getAddedSubList()) {
							if (node instanceof EdenFile) {
								EdenFile newEdenFile = (EdenFile) node;
								newEdenFile.setOnMouseClicked(event -> {
									switch (newEdenFile.getEdenFileType()) {
									case FILE:
										InternalWindow textWindow = createTextEditorWindow(newEdenFile);
										desktopPane.addInternalWindow(textWindow);
										break;
									case FOLDER:
										dirFlowPane.getChildren().clear();
										addEdenFilesToBrowser(dirFlowPane, newEdenFile.getEdenFile().listFiles());
										break;
									}
								});
							}
						}
					}
				}
			}
		});

		Button homeButton = new Button();
		homeButton.setGraphic(FontIcon.of(Material2AL.HOME));
		homeButton.setOnAction(event -> {
			dirFlowPane.getChildren().clear();
			addEdenFilesToBrowser(dirFlowPane,
					EdenFileUtils.getUserDirectoryFiles(edenSceneLogic.getCurrentLoggedInUser()));
		});

		fbRoot.setTop(homeButton);
		fbRoot.setCenter(scrollpane);
		return fbRoot;
	}

	public InternalWindow createTextEditorWindow(EdenFile edenFile) {
		String textContent = EdenFileUtils.getFileAsString(edenFile.getEdenFile());
		TextArea textEditor = new TextArea();
		textEditor.setMaxSize(600, 600);
		textEditor.setText(textContent);
		textEditor.setWrapText(true);

		InternalWindow textWindow = new InternalWindow("edenTextEditor_" + internalTextEditorCap++,
				FontIcon.of(Material2AL.FILE_COPY), "Eden TextEditor - " + edenFile.getEdenFile().getName(),
				textEditor);

		textWindow.setOnCloseRequest(event -> {
			// Save the file
			Logger.debug("Test");
			EdenFileUtils.saveTextFile(textEditor.getText(), edenFile.getEdenFile());
		});

		return textWindow;
	}

	private void addEdenFilesToBrowser(FlowPane dirFlowPane, File[] userFiles) {
		for (File file : userFiles) {
			EdenFile edenFile = null;
			if (file.isDirectory())
				edenFile = new EdenFile(file, EdenFileType.FOLDER);
			else if (file.isFile())
				edenFile = new EdenFile(file, EdenFileType.FILE);

			dirFlowPane.getChildren().add(edenFile);
		}
	}

}

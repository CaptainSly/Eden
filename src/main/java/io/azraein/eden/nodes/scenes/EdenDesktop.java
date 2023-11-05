package io.azraein.eden.nodes.scenes;

import java.io.File;

import org.controlsfx.control.StatusBar;
import org.kordamp.desktoppanefx.scene.layout.DesktopPane;
import org.kordamp.desktoppanefx.scene.layout.InternalWindow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class EdenDesktop extends EdenScene {

	private DesktopPane desktopPane;
	private StatusBar desktopStatusBar;
	private FlowPane directoryFlowPane;
	private VBox desktopDockBox;

	private InternalWindow edenFileBrowser;
	private int internalCount = 0;

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
			// TODO: Figure out how to bring the filebrowser back using the original window
			InternalWindow w = new InternalWindow("window-edenFileBrowser_" + internalCount++,
					FontIcon.of(Material2AL.FOLDER), "Eden FileBrowser", getFileBrowserContent());
			desktopPane.addInternalWindow(w);
			directoryFlowPane.getChildren().clear();
			addEdenFilesToBrowser(directoryFlowPane,
					EdenFileUtils.getUserDirectoryFiles(edenSceneLogic.getCurrentLoggedInUser()));
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
		directoryFlowPane = new FlowPane();
		ScrollPane scrollpane = new ScrollPane(directoryFlowPane);
		fbRoot.setMaxWidth(640);
		fbRoot.setMaxHeight(480);

		directoryFlowPane.setPadding(new Insets(25));

		edenSceneLogic.getCurrentEdenUser().addListener(new ChangeListener<EdenUser>() {

			@Override
			public void changed(ObservableValue<? extends EdenUser> observable, EdenUser oldValue, EdenUser newValue) {

				if (newValue == null) {
					directoryFlowPane.getChildren().clear();
				} else {
					// It's not null fill the pane
					directoryFlowPane.getChildren().clear();
					addEdenFilesToBrowser(directoryFlowPane,
							EdenFileUtils.getUserDirectoryFiles(edenSceneLogic.getCurrentLoggedInUser()));
				}

			}
		});

		directoryFlowPane.getChildren().addListener(new ListChangeListener<Node>() {

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
										// Time to figure out what type of file.
										// Check to see if file has extension.
										String fileName = newEdenFile.getEdenFile().getName();
										if (fileName.contains(".")) {
											// Chances are we have an extension at the end.
											int fileNameLength = fileName.length();
											String fileExtension = fileName.substring(fileNameLength - 4,
													fileNameLength);
											if (fileExtension.equals(".txt")) {
												desktopPane.addInternalWindow(createTextEditorWindow(newEdenFile));
											} else if (fileExtension.equals(".mp3")) {
												desktopPane.addInternalWindow(createAudioMediaWindow(newEdenFile));
											} else if (fileExtension.equals(".mp4")) {
												desktopPane.addInternalWindow(createVideoMediaWindow(newEdenFile));
											} else if (fileExtension.equals(".jpg") || fileExtension.equals(".png")) {
												desktopPane.addInternalWindow(createImageMediaWindow(newEdenFile));
											}

										} else
											desktopPane.addInternalWindow(createTextEditorWindow(newEdenFile));
										break;
									case FOLDER:
										directoryFlowPane.getChildren().clear();
										addEdenFilesToBrowser(directoryFlowPane, newEdenFile.getEdenFile().listFiles());
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
			directoryFlowPane.getChildren().clear();
			addEdenFilesToBrowser(directoryFlowPane,
					EdenFileUtils.getUserDirectoryFiles(edenSceneLogic.getCurrentLoggedInUser()));
		});

		fbRoot.setTop(homeButton);
		fbRoot.setCenter(scrollpane);
		return fbRoot;
	}

	public InternalWindow createImageMediaWindow(EdenFile edenFile) {
		ImageView imageView = new ImageView(new Image(edenFile.getEdenFile().toURI().toString()));
		ScrollPane scrollPane = new ScrollPane(imageView);
		
		InternalWindow w = new InternalWindow("edenImageWindow_" + internalCount++,
				FontIcon.of(Material2AL.IMAGE), "Eden ImageViewer - " + edenFile.getEdenFile().getName(), scrollPane);
		
		return w;
	}

	public InternalWindow createAudioMediaWindow(EdenFile edenFile) {
		Media media = new Media(edenFile.getEdenFile().toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);

		mediaPlayer.setAutoPlay(true);

		Slider volumeSlider = new Slider(0, 1, 0.1);
		volumeSlider.setValue(0.25f);
		mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

		VBox box = new VBox();

		Label mediaLbl = new Label(edenFile.getEdenFile().getName());
		mediaLbl.setGraphic(FontIcon.of(Material2AL.AUDIOTRACK));

		HBox audioButtons = new HBox();

		Button playAudio = new Button();
		playAudio.setGraphic(FontIcon.of(Material2MZ.PLAY_ARROW));
		playAudio.setOnAction(event -> mediaPlayer.play());

		Button pauseAudio = new Button();
		pauseAudio.setGraphic(FontIcon.of(Material2MZ.PAUSE_CIRCLE_FILLED));
		pauseAudio.setOnAction(event -> mediaPlayer.pause());

		audioButtons.getChildren().addAll(playAudio, pauseAudio);

		box.getChildren().addAll(mediaLbl, audioButtons, volumeSlider);

		InternalWindow mediaWindow = new InternalWindow("edenMediaWindow_" + internalCount++,
				FontIcon.of(Material2AL.AUDIOTRACK), "Eden MediaPlayer - " + edenFile.getEdenFile().getName(), box);

		mediaWindow.setOnCloseRequest(event -> {
			mediaPlayer.stop();
		});

		return mediaWindow;
	}

	public InternalWindow createVideoMediaWindow(EdenFile edenFile) {
		Media media = new Media(edenFile.getEdenFile().toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);

		mediaPlayer.setAutoPlay(true);

		mediaView.fitWidthProperty().set(640);
		mediaView.fitHeightProperty().set(480);

		// Media Controls
		Slider volumeSlider = new Slider(0, 1, 0.1);
		volumeSlider.setValue(0.25f);
		mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

		Slider durationSlider = new Slider();

		HBox volumeBox = new HBox();
		HBox durationBox = new HBox();

		volumeBox.getChildren().addAll(new Label("Volume: "), volumeSlider);
		durationBox.getChildren().addAll(new Label("Time: "), durationSlider);

		Button playVideo = new Button();
		playVideo.setGraphic(FontIcon.of(Material2MZ.PLAY_ARROW));
		playVideo.setOnAction(event -> mediaPlayer.play());

		Button pauseVideo = new Button();
		pauseVideo.setGraphic(FontIcon.of(Material2MZ.PAUSE_CIRCLE_FILLED));
		pauseVideo.setOnAction(event -> mediaPlayer.pause());

		HBox controlBox = new HBox();
		controlBox.getChildren().addAll(playVideo, pauseVideo);

		VBox mainBox = new VBox();
		mainBox.getChildren().addAll(mediaView, volumeBox, durationBox, controlBox);

		InternalWindow w = new InternalWindow("eden_videoMediaWindow_" + internalCount++,
				FontIcon.of(Material2AL.FEATURED_VIDEO), "Eden VideoPlayer - " + edenFile.getEdenFile().getName(),
				mainBox);

		w.setOnCloseRequest(event -> mediaPlayer.stop());

		return w;
	}

	public InternalWindow createTextEditorWindow(EdenFile edenFile) {
		String textContent = EdenFileUtils.getFileAsString(edenFile.getEdenFile());
		TextArea textEditor = new TextArea();
		textEditor.setText(textContent);
		textEditor.setWrapText(true);

		InternalWindow textWindow = new InternalWindow("edenTextEditor_" + internalCount++,
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
